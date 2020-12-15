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
	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

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

		turnXAgrXProd.map(new MapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String map(String value) {
				log.info("Map_1_turnXAgrXProd: Got value: " + value);
				TurnXAgrXProd turnXAgrXProdRec = new TurnXAgrXProd(value);
				DynamoDBMapper mapper = new DynamoDBMapper(client);				
				try {

					Bucket bucketAgg = mapper.load(Bucket.class, turnXAgrXProdRec.getCustomerId());
					Bucket bucketResult = new Bucket(bucketAgg, turnXAgrXProdRec);

					log.info("Map_1_turnXAgrXProd: Collected value: " + value + ", turnXAgrXProdRec: "
							+ turnXAgrXProdRec + ", bucketAgg: " + bucketAgg + ", bucketResult: " + bucketResult);
					mapper.save(bucketResult);
					return bucketResult.toString();					
				} catch (Exception ex) {
					log.info("Map_1_turnXAgrXProd exception: ", ex);
					Bucket bucketResult = new Bucket(turnXAgrXProdRec);
					log.info("Map_1_turnXAgrXProd: value: " + value + ", turnXAgrXProdRec: " + turnXAgrXProdRec
							+ ", bucketResult: " + bucketResult);
					mapper.save(bucketResult);
					return bucketResult.toString();
				}
			}
		}).addSink(createSinkFromStaticConfig());

		env.execute("BucketAgr v.1.0.0");
	}

}
