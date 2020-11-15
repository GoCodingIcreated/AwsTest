package gbc.aws.kinesis.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesisanalytics.model.KinesisStreamsInput;

import gbc.aws.kinesis.schemas.Authorization;
import gbc.aws.kinesis.schemas.AuthorizationType;
import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.ProjectSchema;

/**
 * A basic Kinesis Data Analytics for Java application with Kinesis data streams
 * as source and sink.
 */

public class AuthLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "test_in_auth_ss";
	private static final String outputStreamName = "AUTH_FILTERED";

	private static final List<String> allowedAuthType = new ArrayList<>(
			Arrays.asList(new String[] { "authorization_type_10000015", "authorization_type_10000017" }));
	private static final Logger log = LoggerFactory.getLogger(AuthLookUp.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<String> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName, new SimpleStringSchema(), inputProperties));
	}

	private static FlinkKinesisProducer<AuthorizationXType> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty("AggregationEnabled", "false");
		FlinkKinesisProducer<AuthorizationXType> sink = new FlinkKinesisProducer<AuthorizationXType>(
				new ProjectSchema<>(AuthorizationXType.class), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		/*
		 * if you would like to use runtime configuration properties, uncomment the
		 * lines below DataStream<String> input =
		 * createSourceFromApplicationProperties(env);
		 */
		DataStream<String> input = createSourceFromStaticConfig(env);
		DataStream<AuthorizationXType> auth = input.flatMap((value, coll) -> {
			Authorization authRec = new Authorization(value);
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			AuthorizationType authType = mapper.load(AuthorizationType.class, authRec.getAuthorizationTypeId());
			AuthorizationXType authWithType = new AuthorizationXType(authRec, authType.getAuthorizationTypeNm());

			if (allowedAuthType.contains(authWithType.getAuthorizationTypeNm())) {
				log.info("Map 1: Collected value: " + value + ", authRec: " + authRec + ", authType: " + authType
						+ ", authWithType: " + authWithType);
				coll.collect(authWithType);
			} else {
				log.info("Map 1: Not collected value: " + value + ", authRec: " + authRec + ", authType: " + authType
						+ ", authWithType: " + authWithType);
			}
			// return authWithType;
		});
		/*
		 * if you would like to use runtime configuration properties, uncomment the
		 * lines below input.addSink(createSinkFromApplicationProperties())
		 */
		auth.addSink(createSinkFromStaticConfig());

		env.execute("Authorization with LookUp v.1.0.8.");
	}
}
