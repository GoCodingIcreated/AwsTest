package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
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

import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.ClearingXType;
import gbc.aws.kinesis.schemas.Transaction;

public class AuthXClrSS {

	private static final Logger log = LoggerFactory.getLogger(AuthXClrSS.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
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

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName1, new SimpleStringSchema(), inputProperties));
	}

	private static DataStream<String> createSource2FromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

		return env.addSource(new FlinkKinesisConsumer<>(inputStreamName2, new SimpleStringSchema(), inputProperties));
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

		DataStream<String> auth_x_type = createSource1FromStaticConfig(env);
		DataStream<String> clr_x_type = createSource2FromStaticConfig(env);

		auth_x_type.map(new MapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String map(String value) {
				log.info("Map_1_auth_x_type: Got value: " + value);
				AuthorizationXType authXTypeRec = new AuthorizationXType(value);
				DynamoDBMapper mapper = new DynamoDBMapper(client);
				mapper.save(authXTypeRec);
				try {

					ClearingXType clrXType = mapper.load(ClearingXType.class, authXTypeRec.getAuthorizationId());
					Transaction tran = new Transaction(authXTypeRec, clrXType);

					log.info("Map_1_auth_x_type: Collected value: " + value + ", authXTypeRec: " + authXTypeRec
							+ ", clrXType: " + clrXType + ", tran: " + tran);
					return tran.toString();
				} catch (Exception ex) {
					log.info("Map_1_auth_x_type exception: ", ex);
					Transaction tran = new Transaction(authXTypeRec, new ClearingXType());
					log.info("Map_1_auth_x_type: value: " + value + ", authXTypeRec: " + authXTypeRec + ", tran: "
							+ tran);
					return tran.toString();
				}
			}
		}).addSink(createSinkFromStaticConfig());

		clr_x_type.map(new MapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String map(String value) {
				log.info("Map_1_clr_x_type: Got value: " + value);
				ClearingXType clrXTypeRec = new ClearingXType(value);
				DynamoDBMapper mapper = new DynamoDBMapper(client);
				mapper.save(clrXTypeRec);
				try {

					AuthorizationXType authXType = mapper.load(AuthorizationXType.class,
							clrXTypeRec.getAuthorizationId());
					Transaction tran = new Transaction(authXType, clrXTypeRec);

					log.info("Map_1_clr_x_type: Collected value: " + value + ", clrXTypeRec: " + clrXTypeRec
							+ ", authXType: " + authXType + ", tran: " + tran);
					return tran.toString();
				} catch (Exception ex) {
					log.info("Map_1_clr_x_type exception: ", ex);
					Transaction tran = new Transaction(new AuthorizationXType(), clrXTypeRec);
					log.info("Map_1_clr_x_type: value: " + value + ", clrXTypeRec: " + clrXTypeRec + ", tran: " + tran);
					return tran.toString();
				}
			}
		}).addSink(createSinkFromStaticConfig());

		env.execute("AWS SSJoin v1.0.4");
	}

}
