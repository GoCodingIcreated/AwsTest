package gbc.aws.kinesis.streams;

import java.util.Properties;

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

import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.ClearingXType;
import gbc.aws.kinesis.schemas.ProjectSchema;
import gbc.aws.kinesis.schemas.Transaction;

public class ClrXAuthLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "CLR_X_TYPE";
	private static final String outputStreamName = "TRANSACTIONS";

	private static final Logger log = LoggerFactory.getLogger(ClrXAuthLookUp.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<ClearingXType> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);

		return env.addSource(
				new FlinkKinesisConsumer<>(inputStreamName, new ProjectSchema<>(ClearingXType.class), inputProperties));
	}

	private static FlinkKinesisProducer<Transaction> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty("AggregationEnabled", "false");
		FlinkKinesisProducer<Transaction> sink = new FlinkKinesisProducer<Transaction>(
				new ProjectSchema<>(Transaction.class), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<ClearingXType> input = createSourceFromStaticConfig(env);
		DataStream<Transaction> trans = input.flatMap((clrXType, coll) -> {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			AuthorizationXType authXType = mapper.load(AuthorizationXType.class, clrXType.getAuthorizationId());
			if (authXType != null) {
				Transaction trn = new Transaction(authXType, clrXType);
				coll.collect(trn);
				log.info("Map 1: Collected clrXType: " + clrXType + ", authXType: " + authXType + ", trn: " + trn);
			} else {
				log.info("Map 1: Not collected clrXType: " + clrXType + ", authXType: " + authXType);
			}

		});

		trans.addSink(createSinkFromStaticConfig());
		env.execute("ClrXAuthLookUp v.1.0.0.");
	}
}
