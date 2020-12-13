package gbc.aws.kinesis.streams;

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

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Bucket;
import gbc.aws.kinesis.schemas.BucketXCustomer;
import gbc.aws.kinesis.schemas.Customer;


public class BucketLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "BUCKET";
	private static final String outputStreamName = "BUCKET_X_CUSTOMER";

	private static final Logger log = LoggerFactory.getLogger(BucketLookUp.class);

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
		DataStream<String> bucketXCustomer = input.map((bucketStr) -> {
			Bucket bucket = new Bucket(bucketStr);
			
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			try {
				Customer customer = mapper.load(Customer.class, bucket.getCustomerId());
				BucketXCustomer bucketXCust = new BucketXCustomer(bucket, customer);
				log.info("Map_1: bucket: " + bucket + ", customer: " + customer + ", bucketXCust: " + bucketXCust);
				return bucketXCust.toString();
			}
			catch(Exception ex) {
				log.error("Map_1 exception: ", ex);
				Customer customer = new Customer();
				BucketXCustomer bucketXCust = new BucketXCustomer(bucket, customer);
				log.error("Map_1: bucket: " + bucket + ", customer: " + customer + ", bucketXCust: " + bucketXCust);
				return bucketXCust.toString();
			}
		});

		bucketXCustomer.addSink(createSinkFromStaticConfig());
		env.execute("BucketLookUp v.1.0.0.");
	}
}
