package com.amazonaws.services.kinesisanalytics;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;

import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Bucket;
import gbc.aws.kinesis.schemas.ProjectSchema;
import gbc.aws.kinesis.schemas.TransactionXCard;
import gbc.aws.kinesis.schemas.Turn;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * A basic Kinesis Data Analytics for Java application with Kinesis data
 * streams as source and sink.
 */
public class TurnAgr {
	
	private static final Logger log = LoggerFactory.getLogger(TurnAgr.class);
	
    private static final String region = "us-east-1";
    private static final String inputStreamName = "test_in_auth_ss";
    private static final String outputStreamName = "test_out_auth_ss";
	private static final String aws_access_key_id = AwsKinesisData.getAwsAccessKeyId();
	private static final String aws_secret_access_key = AwsKinesisData.getAwsSecretAccessKey();

    private static DataStream<TransactionXCard> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
        Properties inputProperties = new Properties();
        inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
        inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
        inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        return env.addSource(new FlinkKinesisConsumer<>(inputStreamName, new ProjectSchema<>(TransactionXCard.class), inputProperties));
    }

    private static FlinkKinesisProducer<Turn> createSinkFromStaticConfig() {
        Properties outputProperties = new Properties();
        outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        outputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
        outputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
        outputProperties.setProperty("AggregationEnabled", "false");

        FlinkKinesisProducer<Turn> sink = new FlinkKinesisProducer<>(new ProjectSchema<>(Turn.class), outputProperties);
        sink.setDefaultStream(outputStreamName);
        sink.setDefaultPartition("0");
        return sink;
    }


    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /* if you would like to use runtime configuration properties, uncomment the lines below
         * DataStream<String> input = createSourceFromApplicationProperties(env);
         */
        DataStream<TransactionXCard> turn = createSourceFromStaticConfig(env);
//         
//		DataStream<String> auth = input.map((value) -> {			
//			String str[] = value.split(";");
//			HashMap<String,String> map = new HashMap<String, String>();
//			log.info("Got records: " + str.length);
//	        for(int i=1;i<str.length;i++){
//	            map.put(String.valueOf(i), str[i]);
//	            log.info("Got subvalue: " + str[i]);
//	        }
//			log.info("Got value: " + value + ", transformed to: " + map.toString());
//			return map.toString();
//		});
		
//		input.flatMap(new serialToTuple())
//			.keyBy(0)
//			.timeWindow(Time.minutes(5))
//			.sum(2)
//			.map((value) -> { 
//				String result = value.f0.toString() + "," + value.f2.toString() + "\n";
//				log.info("Got result: " + result);
//				return result;
//				})
//			.addSink(createSinkFromStaticConfig());
        
		turn // .flatMap(new serialToTuple())
		.keyBy((value) -> {	log.info("Got key value: " + value.getCardNumber());
					return value.getCardNumber();
					}
				)
		.process(new PseudoWindow(Time.minutes(5)))
//		.map((value) -> { 
//			String result = value.f0.toString() + "," + value.f1.toString() + "\n";
//			log.info("Got result: " + result);
//			return result;
//			})
		.addSink(createSinkFromStaticConfig());
		
		

//		auth.keyBy(0)
//			.timeWindow(Time.seconds(60))
//			.sum(2)
//			.map((value) -> {							
//				return value.toString();
//			})
//			;
		
        /* if you would like to use runtime configuration properties, uncomment the lines below
         * input.addSink(createSinkFromApplicationProperties())
         */
//        auth.addSink(createSinkFromStaticConfig());

        env.execute("Flink Streaming Java API Skeleton");
    }
    
    public static class PseudoWindow extends 
    KeyedProcessFunction<String, TransactionXCard, Turn> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final long durationMsec;
		
		public PseudoWindow(Time duration) {
		    this.durationMsec = duration.toMilliseconds();
		}
		
		private transient MapState<String, Double> sumOfTransaction;
		
		@Override
		public void open(Configuration conf) {
		    MapStateDescriptor<String, Double> sumDesc =
		            new MapStateDescriptor<>("sumOfTransaction", String.class, Double.class);
		    sumOfTransaction = getRuntimeContext().getMapState(sumDesc);
		}
		
		@Override
		public void processElement(
				TransactionXCard record,
		        Context ctx,
		        Collector<Turn> out) throws Exception {
			
			long eventTime = System.currentTimeMillis();
		    TimerService timerService = ctx.timerService();

		    if (eventTime <= timerService.currentWatermark()) {
		        // This event is late; its window has already been triggered.
		    } else {
		
		        long endOfWindow = (eventTime - (eventTime % durationMsec) + durationMsec - 1);

//		        timerService.registerEventTimeTimer(endOfWindow);
		        timerService.registerProcessingTimeTimer(endOfWindow);

		        // Add this fare's tip to the running total for that window.
		        String stateKey = record.getCardNumber(); //+ String.valueOf(endOfWindow);
		        Double sum = sumOfTransaction.get(stateKey);
		        if (sum == null) {
		            sum = 0.0;
		        }
		        sum += record.getTransactionAmt();
		        sumOfTransaction.put(stateKey, sum);
			    Turn result = new Turn();
			    log.info("Got timers: eventTime: " + eventTime + " endOfWindow: " + endOfWindow + " currentWatermark: " + timerService.currentWatermark());
			    log.info("Got result: " + stateKey + ":" + sum);
			    
			    result.setAgreementId(record.getAgreementId());
//			    result.setCardFinishDt(finishDt);;
//			    result.setCardStartDt(startDt);
			    result.setCardId(record.getCardId());
			    result.setCardNumber(stateKey);
//			    result.setMonthDt(monthDt);
			    result.setTurnAmt(sum);
			    out.collect(result);
		    }
		}
		
		@Override
		public void onTimer(long timestamp, 
		        OnTimerContext context, 
		        Collector<Turn> out) throws Exception {
			
		    String key = context.getCurrentKey();
			log.info("PseudoWindow timer expired! Key: " + key);
		    // Look up the result for the hour that just ended.
//		    Double sumOfTransaction = this.sumOfTransaction.get(key);
	
//		    Turn result = new Turn();
//		    out.collect(result);
		    this.sumOfTransaction.clear();
		
		}
}
    public static final class serialToTuple
    implements FlatMapFunction<String, Tuple8<Integer, Integer, Double, Integer, String, String, String, String>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void flatMap(String value, Collector<Tuple8<Integer, Integer, Double, Integer, String, String, String, String>> out) {
		    String str[] = value.split(";");
		    out.collect(new Tuple8<>(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Double.parseDouble(str[2]), Integer.parseInt(str[3]), str[4], str[5], str[6], str[7]));	
		}
	}
}
