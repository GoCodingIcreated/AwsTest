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
import com.amazonaws.services.kinesisanalytics.model.KinesisStreamsInput;

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Clearing;
import gbc.aws.kinesis.schemas.ClearingType;
import gbc.aws.kinesis.schemas.ClearingXType;


public class ClrLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "CLEARING";
	private static final String outputStreamName = "CLR_X_TYPE";

	private static final Logger log = LoggerFactory.getLogger(ClrLookUp.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

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
		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(
				new SimpleStringSchema(), outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> input = createSourceFromStaticConfig(env);
		DataStream<String> clr = input.map(new MapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String map(String value) throws Exception {
				Clearing clrRec = new Clearing(value, ";", true);
				DynamoDBMapper mapper = new DynamoDBMapper(client);
				try {
					ClearingType clrType = mapper.load(ClearingType.class, clrRec.getclearingTypeId());
					ClearingXType clrWithType = new ClearingXType(clrRec, clrType.getClearingTypeNm());
					log.info("Map 1: Value: " + value + ", clrRec: " + clrRec + ", clrType: " + clrType + ", clrWithType: "
							+ clrWithType);
					return clrWithType.toString();
				}
				catch (Exception ex) {
					ClearingXType clrWithType = new ClearingXType(clrRec, ""); 
					log.error("Map 1: Value: " + value + ", clrRec: " + clrRec + ", clrType: null, clrWithType: "
							+ clrWithType);
					return clrWithType.toString();
				}
				

								
			};
		});
				
				

		clr.addSink(createSinkFromStaticConfig());
		env.execute("ClrLookUp v.1.0.8.");
	}
}
