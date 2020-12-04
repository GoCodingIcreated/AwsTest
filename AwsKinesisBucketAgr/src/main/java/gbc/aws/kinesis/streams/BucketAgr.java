package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Bucket;
import gbc.aws.kinesis.schemas.TurnXAgrXProd;

public class BucketAgr {

	private static final Logger log = LoggerFactory.getLogger(BucketAgr.class);

	private static final String region = "us-east-1";
	private static final String inputStreamName = "TURN_X_AGR_X_PROD";
	private static final String outputStreamName = "BUCKET";
	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<String> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName, new SimpleStringSchema(), inputProperties));
	}

	private static FlinkKinesisProducer<String> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		outputProperties.setProperty("AggregationEnabled", "false");

		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(new SimpleStringSchema(),
				outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		return sink;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> turnXAgrXProd = createSourceFromStaticConfig(env);

		turnXAgrXProd.keyBy((value) -> {
			TurnXAgrXProd turn = new TurnXAgrXProd(value);
			log.info("Got key value: " + turn.getCustomerId());
			return turn.getCustomerId();
		}).process(new PseudoWindow(Time.days(30))).addSink(createSinkFromStaticConfig());

		env.execute("Flink Streaming Java API Skeleton");
	}

	public static class PseudoWindow extends KeyedProcessFunction<Integer, String, String> {

		private static final long serialVersionUID = 1L;
		private final long durationMsec;

		public PseudoWindow(Time duration) {
			this.durationMsec = duration.toMilliseconds();
		}

		private transient MapState<Integer, Double> sumOfTransaction;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<Integer, Double> sumDesc = new MapStateDescriptor<>("sumOfTransaction", Integer.class,
					Double.class);
			sumOfTransaction = getRuntimeContext().getMapState(sumDesc);
		}

		@Override
		public void processElement(String record, Context ctx, Collector<String> out) throws Exception {
			TurnXAgrXProd turn = new TurnXAgrXProd(record);
			long eventTime = System.currentTimeMillis();
			TimerService timerService = ctx.timerService();

			if (eventTime <= timerService.currentWatermark()) {
				// This event is late; its window has already been triggered.
			} else {

				long endOfWindow = (eventTime - (eventTime % durationMsec) + durationMsec - 1);

				timerService.registerProcessingTimeTimer(endOfWindow);

				Integer stateKey = turn.getCustomerId();
				Double sum = sumOfTransaction.get(stateKey);
				if (sum == null) {
					sum = 0.0;
				}
				sum += turn.getTurnAmt();
				sumOfTransaction.put(stateKey, sum);
				Bucket result = new Bucket(turn);
				log.info("Got timers: eventTime: " + eventTime + " endOfWindow: " + endOfWindow + " currentWatermark: "
						+ timerService.currentWatermark());
				log.info("Got result: CustomerId: " + stateKey + " getTurnAmt: " + sum);
				log.info("Bucket: " + result);
				out.collect(result.toString());
			}
		}

		@Override
		public void onTimer(long timestamp, OnTimerContext context, Collector<String> out) throws Exception {

			Integer key = context.getCurrentKey();
			log.info("PseudoWindow timer expired! Key: " + key);
			// Look up the result for the hour that just ended.
//		    Double sumOfTransaction = this.sumOfTransaction.get(key);

			Bucket result = new Bucket();
			out.collect(result.toString());
			this.sumOfTransaction.clear();

		}
	}
}
