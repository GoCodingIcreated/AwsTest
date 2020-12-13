package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
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

public class AuthLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "AUTHORIZATION";
	private static final String outputStreamName = "AUTH_X_TYPE";

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

	private static FlinkKinesisProducer<String> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty("AggregationEnabled", "false");
		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(new SimpleStringSchema(),
				outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> input = createSourceFromStaticConfig(env);
		DataStream<String> auth = input.flatMap(new FlatMapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void flatMap(String value, Collector<String> out) throws Exception {
				log.info("Map_1: Got value: " + value);
				Authorization authRec = new Authorization(value, ";", true);
				try {
					
					DynamoDBMapper mapper = new DynamoDBMapper(client);
					AuthorizationType authType = mapper.load(AuthorizationType.class, authRec.getAuthorizationTypeId());
					AuthorizationXType authWithType = new AuthorizationXType(authRec, authType.getAuthorizationTypeNm());
	
					log.info("Map_1: Collected value: " + value + ", authRec: " + authRec + ", authType: " + authType
							+ ", authWithType: " + authWithType + ", AuthorizationTypeNm: " + authWithType.getAuthorizationTypeNm());
					out.collect(authWithType.toString());
				} catch(Exception ex) {
					log.error("Map_1 exception: ", ex);
					AuthorizationXType authWithType = new AuthorizationXType(authRec, new AuthorizationType());
					log.error("Map_1: value: " + value + ", authRec: " + authRec + ", authWithType: " + authWithType);
					out.collect(authWithType.toString());
				}
			}
		});

		auth.addSink(createSinkFromStaticConfig());

		env.execute("AuthLookUp v.1.13");		
	}
}
