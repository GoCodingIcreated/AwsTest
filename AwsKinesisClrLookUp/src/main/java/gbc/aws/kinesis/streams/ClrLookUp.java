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

import com.amazonaws.services.kinesisanalytics.model.KinesisStreamsInput;

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Clearing;
import gbc.aws.kinesis.schemas.ClearingType;
import gbc.aws.kinesis.schemas.ClearingXType;



public class ClrLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "CLEARING";
	private static final String outputStreamName = "CLR_X_TYPE";
	private static final String inputLookUpStreamName = "CLR_TYPE";

	private static final Logger log = LoggerFactory.getLogger(ClrLookUp.class);


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
		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(
				new SimpleStringSchema(), outputProperties);
		sink.setDefaultStream(streamName);
		sink.setDefaultPartition("0");
		new KinesisStreamsInput();
		return sink;
	}

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> input = createSourceFromStaticConfig(env, inputStreamName);
		DataStream<String> inputLookUp = createSourceFromStaticConfig(env, inputLookUpStreamName);

		input.connect(inputLookUp).keyBy((value) -> {
			Clearing clrRec = new Clearing(value);
			log.info("Got Clearing key value: " + clrRec.getClearingTypeId());
			return clrRec.getClearingTypeId();
		}, (value) -> {
			ClearingType clrType = new ClearingType(value);
			log.info("Got ClearingType key value: " + clrType.getClearingTypeId());
			return clrType.getClearingTypeId();
		}).process(new PseudoWindow()).addSink(createSinkFromStaticConfig(outputStreamName));


	
		env.execute("ClrLookUp v.1.0.8.");
	}
	
	

	public static class PseudoWindow extends CoProcessFunction<String, String, String> {

		private static final long serialVersionUID = 1L;

		public PseudoWindow() {

		}

		private transient MapState<Integer, ClearingType> clrState;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<Integer, ClearingType> clrLookUpState = new MapStateDescriptor<>("clrLookUpState",
					Integer.class, ClearingType.class);
			clrState = getRuntimeContext().getMapState(clrLookUpState);
		}

		@Override
		public void processElement1(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Got record: " + record);
			Clearing clr = new Clearing(record, ";", true);			
			Integer stateKey = clr.getClearingTypeId();
			ClearingType rec = new ClearingType(clrState.get(stateKey));
			
			log.info("Got state: record: " + record + ", clr: " + clr + ", ClearingType: " + rec);
			ClearingXType result = new ClearingXType(clr, rec);
			log.info("ProcessElement1 record: " + record + ", clr: " + clr + ", result: " + result);
			out.collect(result.toString());
		}

		@Override
		public void processElement2(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Map 1: Got record: " + record);
			ClearingType clrLookUp = new ClearingType(record);
			clrState.put(clrLookUp.getClearingTypeId(), clrLookUp);
			log.info("Map 1: Stated record: " + record + ", clrLookUp: " + clrLookUp);
		}

	}
}
