package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple8;
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
import gbc.aws.kinesis.schemas.TransactionXCard;
import gbc.aws.kinesis.schemas.Turn;

public class TurnAgr {

	private static final Logger log = LoggerFactory.getLogger(TurnAgr.class);

	private static final String region = "us-east-1";
	private static final String inputStreamName = "TRN_X_CARD";
	private static final String outputStreamName = "TURN";
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

		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<>(new SimpleStringSchema(), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		return sink;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> turn = createSourceFromStaticConfig(env);

		turn.keyBy((value) -> {
			TransactionXCard trn = new TransactionXCard(value);
			log.info("Got key value: " + trn.getCardNumber());
			return trn.getCardNumber();
		}).process(new PseudoWindow(Time.days(30))).addSink(createSinkFromStaticConfig());

		env.execute("Flink Streaming Java API Skeleton");
	}

	public static class PseudoWindow extends KeyedProcessFunction<String, String, String> {

		private static final long serialVersionUID = 1L;
		private final long durationMsec;

		public PseudoWindow(Time duration) {
			this.durationMsec = duration.toMilliseconds();
		}

		private transient MapState<String, Double> sumOfTransaction;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<String, Double> sumDesc = new MapStateDescriptor<>("sumOfTransaction", String.class,
					Double.class);
			sumOfTransaction = getRuntimeContext().getMapState(sumDesc);
		}

		@Override
		public void processElement(String record, Context ctx, Collector<String> out) throws Exception {
			TransactionXCard trn = new TransactionXCard(record);
			long eventTime = System.currentTimeMillis();
			TimerService timerService = ctx.timerService();

			if (eventTime <= timerService.currentWatermark()) {
				// This event is late; its window has already been triggered.
			} else {

				long endOfWindow = (eventTime - (eventTime % durationMsec) + durationMsec - 1);

				timerService.registerProcessingTimeTimer(endOfWindow);

				String stateKey = trn.getCardNumber();
				Double sum = sumOfTransaction.get(stateKey);
				if (sum == null) {
					sum = 0.0;
				}
				sum += trn.getTransactionAmt();
				sumOfTransaction.put(stateKey, sum);
				Turn result = new Turn(trn, sum, "2020-11-01");
				log.info("Got timers: eventTime: " + eventTime + " endOfWindow: " + endOfWindow + " currentWatermark: "
						+ timerService.currentWatermark());
				log.info("Got result: " + stateKey + ":" + sum);
				log.info("Turn: " + result);
				/*
				 * result.setAgreementId(trn.getAgreementId()); //
				 * result.setCardFinishDt(finishDt);; // result.setCardStartDt(startDt);
				 * result.setCardId(trn.getCardId()); result.setCardNumber(stateKey); //
				 * result.setMonthDt(monthDt); result.setTurnAmt(sum);
				 */
				out.collect(result.toString());
			}
		}

		@Override
		public void onTimer(long timestamp, OnTimerContext context, Collector<String> out) throws Exception {

			String key = context.getCurrentKey();
			log.info("PseudoWindow timer expired! Key: " + key);
			this.sumOfTransaction.clear();

		}
	}

	public static final class serialToTuple implements
			FlatMapFunction<String, Tuple8<Integer, Integer, Double, Integer, String, String, String, String>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void flatMap(String value,
				Collector<Tuple8<Integer, Integer, Double, Integer, String, String, String, String>> out) {
			String str[] = value.split(";");
			out.collect(new Tuple8<>(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Double.parseDouble(str[2]),
					Integer.parseInt(str[3]), str[4], str[5], str[6], str[7]));
		}
	}
}
