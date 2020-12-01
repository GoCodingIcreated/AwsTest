package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.ClearingXType;
import gbc.aws.kinesis.schemas.Transaction;

public class AuthXClrSS {

	private static final Logger log = LoggerFactory.getLogger(AuthXClrSS.class);

	private static final String region = "us-east-1";
	private static final String inputStreamName1 = "AUTH_X_TYPE";
	private static final String inputStreamName2 = "CLR_X_TYPE";
	private static final String outputStreamName = "TRANSACTION";
	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<String> createSource1FromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName1, new SimpleStringSchema(),
				inputProperties));
	}

	private static DataStream<String> createSource2FromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName2, new SimpleStringSchema(),
				inputProperties));
	}

	private static FlinkKinesisProducer<String> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		outputProperties.setProperty("AggregationEnabled", "false");

		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(
				new SimpleStringSchema(), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		return sink;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> auth_x_type = createSource1FromStaticConfig(env);
		DataStream<String> clr_x_type = createSource2FromStaticConfig(env);

		auth_x_type.connect(clr_x_type)
			.keyBy((value) -> {
				AuthorizationXType auth = new AuthorizationXType(value);
				log.info("Got auth key value: " + auth.getAuthorizationId());
				return auth.getAuthorizationId();
			},
			(value) -> {
				ClearingXType clr = new ClearingXType(value);
				log.info("Got clearing key value: " + clr.getClearingId());
				return clr.getClearingId();
			}).process(new PseudoWindow(Time.minutes(60)))
			.addSink(createSinkFromStaticConfig());

		env.execute("AWS SSJoin v1.0.3");
	}

	public static class PseudoWindow extends CoProcessFunction<String, String, String> {

		private static final long serialVersionUID = 1L;
		private final long durationMsec;
		private Transaction tran;
		
		public PseudoWindow(Time duration) {
			this.durationMsec = duration.toMilliseconds();
		}

		private transient MapState<Integer, String> authState;
		private transient MapState<Integer, String> clrState;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<Integer, String> authState1 = new MapStateDescriptor<>("authState", Integer.class,
					String.class);
			MapStateDescriptor<Integer, String> clrState1 = new MapStateDescriptor<>("clrState", Integer.class,
					String.class);
			authState = getRuntimeContext().getMapState(authState1);
			clrState = getRuntimeContext().getMapState(clrState1);
		}
		
		@Override
		public void processElement1(String record, Context ctx, Collector<String> out) throws Exception {
			AuthorizationXType auth = new AuthorizationXType(record);
			long eventTime = System.currentTimeMillis();
			TimerService timerService = ctx.timerService();

			if (eventTime <= timerService.currentWatermark()) {
				// 11
			} else {

				long endOfWindow = (eventTime - (eventTime % durationMsec) + durationMsec - 1);

				timerService.registerProcessingTimeTimer(endOfWindow);

				Integer stateKey = auth.getAuthorizationId();
				String rec = clrState.get(stateKey);
				if (rec == null) {
					authState.put(stateKey, record);
					tran = new Transaction(new AuthorizationXType(record), new ClearingXType());
				} else {		
					tran = new Transaction(new AuthorizationXType(record), new ClearingXType(rec));
				}
				
				log.info("Got timers: eventTime: " + eventTime + " endOfWindow: " + endOfWindow + " currentWatermark: "
						+ timerService.currentWatermark());
				log.info("Got result: " + stateKey + ":" + rec);
				log.info("Transaction: " + tran);

				out.collect(tran.toString());
			}
		}
		
		@Override
		public void processElement2(String record, Context ctx, Collector<String> out) throws Exception {
			ClearingXType clr = new ClearingXType(record);
			long eventTime = System.currentTimeMillis();
			TimerService timerService = ctx.timerService();

			if (eventTime <= timerService.currentWatermark()) {
				// 11
			} else {

				long endOfWindow = (eventTime - (eventTime % durationMsec) + durationMsec - 1);

				timerService.registerProcessingTimeTimer(endOfWindow);

				Integer stateKey = clr.getAuthorizationId();
				String rec = authState.get(stateKey);
				if (rec == null) {
					clrState.put(stateKey, record);
					tran = new Transaction(new AuthorizationXType(), new ClearingXType(record));
				} else {		
					tran = new Transaction(new AuthorizationXType(rec), new ClearingXType(record));
				}
				
				log.info("Got timers: eventTime: " + eventTime + " endOfWindow: " + endOfWindow + " currentWatermark: "
						+ timerService.currentWatermark());
				log.info("Got result: " + stateKey + ":" + rec);
				log.info("Transaction: " + tran);

				out.collect(tran.toString());
			}
		}

		@Override
		public void onTimer(long timestamp, OnTimerContext context, Collector<String> out) throws Exception {

//			String key = context.getCurrentKey();
			log.info("PseudoWindow timer expired!"); // Key: " + key);
			this.clrState.clear();
			this.authState.clear();

		}
	}

}
