package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gbc.aws.kinesis.schemas.Authorization;
import gbc.aws.kinesis.schemas.AuthorizationType;
import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;

public class AuthLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "AUTHORIZATION";
	private static final String inputLookUpStreamName = "AUTH_TYPE";
	private static final String outputStreamName = "AUTH_X_TYPE";

	// private static final List<String> allowedAuthType = new ArrayList<>(
//			Arrays.asList(new String[] { "authorization_type_10000015", "authorization_type_10000017" }));
	private static final Logger log = LoggerFactory.getLogger(AuthLookUp.class);

	// private static final AmazonDynamoDB client =
	// AmazonDynamoDBClientBuilder.standard().build();

	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<String> createSourceFromStaticConfig(StreamExecutionEnvironment env, String streamName) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);

		return env.addSource(new FlinkKinesisConsumer<>(streamName, new SimpleStringSchema(), inputProperties));
	}

	private static FlinkKinesisProducer<String> createSinkFromStaticConfig(String streamName) {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty("AggregationEnabled", "false");
		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(new SimpleStringSchema(),
				outputProperties);
		sink.setDefaultStream(streamName);
		sink.setDefaultPartition("0");
		return sink;
	}

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> input = createSourceFromStaticConfig(env, inputStreamName);
		DataStream<String> inputLookUp = createSourceFromStaticConfig(env, inputLookUpStreamName);

		input.connect(inputLookUp).keyBy((value) -> {
			Authorization authRec = new Authorization(value);
			log.info("Got Authorization key value: " + authRec.getAuthorizationTypeId());
			return authRec.getAuthorizationTypeId();
		}, (value) -> {
			AuthorizationType authType = new AuthorizationType(value);
			log.info("Got AuthorizationType key value: " + authType.getAuthorizationTypeId());
			return authType.getAuthorizationTypeId();
		}).process(new PseudoWindow()).addSink(createSinkFromStaticConfig(outputStreamName));

		env.execute("AuthLookUp v.1.13");
	}

	public static class PseudoWindow extends CoProcessFunction<String, String, String> {

		private static final long serialVersionUID = 1L;

		public PseudoWindow() {

		}

		private transient MapState<Integer, AuthorizationType> authState;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<Integer, AuthorizationType> authLookUpState = new MapStateDescriptor<>("authLookUpState",
					Integer.class, AuthorizationType.class);
			authState = getRuntimeContext().getMapState(authLookUpState);
		}

		@Override
		public void processElement1(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Got record: " + record);
			Authorization auth = new Authorization(record);			
			Integer stateKey = auth.getAuthorizationId();
			AuthorizationType rec = authState.get(stateKey);
			String output = "";
			for (AuthorizationType a: authState.values()) {
				output += a.toString() + "; ";
			}
			
			log.info("Got state: record: " + record + ", auth: " + auth + ", AuthorizationType: " + rec + ", All_state: " + output);
			AuthorizationXType result = new AuthorizationXType(auth, rec);
			log.info("ProcessElement1 record: " + record + ", auth: " + auth + ", result: " + result);
			out.collect(result.toString());
		}

		@Override
		public void processElement2(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Map 1: Got record: " + record);
			AuthorizationType authLookUp = new AuthorizationType(record);
			authState.put(authLookUp.getAuthorizationTypeId(), authLookUp);
			log.info("Map 1: Stated record: " + record + ", authLookUp: " + authLookUp);
		}

	}
}
