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

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Card;
import gbc.aws.kinesis.schemas.Transaction;
import gbc.aws.kinesis.schemas.TransactionXCard;

public class TrnLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "TRANSACTION";
	private static final String inputLookUpStreamName = "CARD";
	private static final String outputStreamName = "TRAN_X_CARD";

	private static final Logger log = LoggerFactory.getLogger(TrnLookUp.class);

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
			Transaction trnRec = new Transaction(value);
			log.info("Got Transaction key value: " + trnRec.getCardId());
			return trnRec.getCardId();
		}, (value) -> {
			Card card = new Card(value);
			log.info("Got Card key value: " + card.getCardId());
			return card.getCardId();
		}).process(new PseudoWindow())
		.addSink(createSinkFromStaticConfig(outputStreamName));

		env.execute("TrnLookUp v.1.0.0");
	}
	
	public static class PseudoWindow extends CoProcessFunction<String, String, String> {

		private static final long serialVersionUID = 1L;

		public PseudoWindow() {

		}

		private transient MapState<Integer, Card> cardState;

		@Override
		public void open(Configuration conf) {
			MapStateDescriptor<Integer, Card> cardLookUpState = new MapStateDescriptor<>("cardLookUpState",
					Integer.class, Card.class);
			cardState = getRuntimeContext().getMapState(cardLookUpState);
		}

		@Override
		public void processElement1(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Got record: " + record);
			Transaction trn = new Transaction(record, ";");			
			Integer stateKey = trn.getCardId();
			Card rec = new Card(cardState.get(stateKey));
			
			log.info("Got state: record: " + record + ", trn: " + trn + ", Card: " + rec);
			TransactionXCard result = new TransactionXCard(trn, rec);
			log.info("ProcessElement1 record: " + record + ", trn: " + trn + ", result: " + result);
			out.collect(result.toString());
		}

		@Override
		public void processElement2(String record, Context ctx, Collector<String> out) throws Exception {
			log.info("Map 1: Got record: " + record);
			Card cardLookUp = new Card(record);
			cardState.put(cardLookUp.getCardId(), cardLookUp);
			log.info("Map 1: Stated record: " + record + ", cardLookUp: " + cardLookUp);
		}

	}
}
