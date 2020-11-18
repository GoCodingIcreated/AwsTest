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

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Product;
import gbc.aws.kinesis.schemas.ProjectSchema;
import gbc.aws.kinesis.schemas.TurnXAgr;
import gbc.aws.kinesis.schemas.TurnXAgrXProd;

public class TurnXAgrLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "TURN_X_AGR";
	private static final String outputStreamName = "TURN_X_AGR_X_PROD";

	private static final Logger log = LoggerFactory.getLogger(TurnXAgrLookUp.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

	private static DataStream<TurnXAgr> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
		inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);

		return env.addSource(
				new FlinkKinesisConsumer<>(inputStreamName, new ProjectSchema<>(TurnXAgr.class), inputProperties));
	}

	private static FlinkKinesisProducer<TurnXAgrXProd> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty("AggregationEnabled", "false");
		FlinkKinesisProducer<TurnXAgrXProd> sink = new FlinkKinesisProducer<TurnXAgrXProd>(
				new ProjectSchema<>(TurnXAgrXProd.class), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<TurnXAgr> input = createSourceFromStaticConfig(env);
		DataStream<TurnXAgrXProd> turnXAgreementXProduct = input.map((turnXAgr) -> {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			Product prod = mapper.load(Product.class, turnXAgr.getAgreementId());
			TurnXAgrXProd turnXAgrXProd = new TurnXAgrXProd(turnXAgr, prod);
			log.info("Map 1: turnXAgr: " + turnXAgr + ", prod: " + prod + ", turnXAgrXProd: " + turnXAgrXProd);
			return turnXAgrXProd;
		});

		turnXAgreementXProduct.addSink(createSinkFromStaticConfig());
		env.execute("TurnXAgrLookUp v.1.0.0.");
	}
}
