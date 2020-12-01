package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
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
	private static final String outputStreamName = "TRANSACTIONS";
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
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

		DataStream<String> auth = createSource1FromStaticConfig(env);

		DataStream<String> clr = createSource2FromStaticConfig(env);

		auth.join(clr).where(new KeySelector<String, Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Integer getKey(String value) {
				Integer key = new AuthorizationXType(value).getAuthorizationId();
				log.info("AuthXClrSS getKeyAuth value: " + value + ", key: " + key);
				return key;
			}
		}).equalTo(new KeySelector<String, Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Integer getKey(String value) {		
				Integer key = new ClearingXType(value).getAuthorizationId();
				log.info("AuthXClrSS getKeyClr value: " + value + ", key: " + key);
				return key;				
			}
		}).window(TumblingEventTimeWindows.of(Time.minutes(60)))
				.apply(new JoinFunction<String, String, String>() {

					private static final long serialVersionUID = 1L;

					@Override
					public String join(String auth, String clr) {
						Transaction trans = new Transaction(new AuthorizationXType(auth), new ClearingXType(clr));
						log.info("AuthXClrSS transaction: " + trans + ", auth: " + auth + ", clr: " + clr);
						return trans.toString();
					}
				}).addSink(createSinkFromStaticConfig());

		env.execute("AuthXClrSS v1.0.2");
	}
}
