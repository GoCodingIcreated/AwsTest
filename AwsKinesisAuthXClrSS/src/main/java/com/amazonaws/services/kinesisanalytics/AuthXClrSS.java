package com.amazonaws.services.kinesisanalytics;

import gbc.aws.kinesis.schemas.Authorization;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Clearing;
import gbc.aws.kinesis.schemas.ProjectSchema;
import gbc.aws.kinesis.schemas.Transaction;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class AuthXClrSS {
	
	private static final Logger log = LoggerFactory.getLogger(AuthXClrSS.class);
	
    private static final String region = "us-east-1";
    private static final String inputStreamName1 = "test_in_auth_ss";
    private static final String inputStreamName2 = "test_in_post_ss";
    private static final String outputStreamName = "test_out_transction_ss";
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

    private static FlinkKinesisProducer<Transaction> createSinkFromStaticConfig() {
        Properties outputProperties = new Properties();
        outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        outputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
        outputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
        outputProperties.setProperty("AggregationEnabled", "false");

        FlinkKinesisProducer<Transaction> sink = new FlinkKinesisProducer<Transaction>(
				new ProjectSchema<>(Transaction.class), outputProperties);
        sink.setDefaultStream(outputStreamName);
        sink.setDefaultPartition("0");
        return sink;
    }

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);


        DataStream<String> input1 = createSource1FromStaticConfig(env);
		DataStream<Authorization> auth = input1.flatMap((value, coll) -> {
			Authorization authRec = new Authorization(value);
		
			log.info("Authorization Map 1: Collected value: " + value + ", authRec: " + authRec);
			coll.collect(authRec);
			});
		
        DataStream<String> input2 = createSource2FromStaticConfig(env);
		DataStream<Clearing> clr = input2.flatMap((value, coll) -> {
			Clearing clrRec = new Clearing(value);
		
			log.info("Clearing Map 1: Collected value: " + value + ", clrRec: " + clrRec);
			coll.collect(clrRec);
			});
               
		auth.join(clr)
			.where(new KeySelector<Authorization, Integer>() {

				private static final long serialVersionUID = 1L;

				@Override
				  public Integer getKey(Authorization value) {
					  return value.getAuthorizationId();
				  }
		    })
			.equalTo(new KeySelector<Clearing, Integer>() {

				private static final long serialVersionUID = 1L;

				@Override
				  public Integer getKey(Clearing value) {
					return value.getAuthorizationId();
				  }
		    })
			.window(TumblingEventTimeWindows.of(Time.minutes(60)))
			.apply(new JoinFunction<Authorization, Clearing, Transaction>(){

				private static final long serialVersionUID = 1L;

				@Override
		        public Transaction join(Authorization auth, Clearing clr) {
		            Transaction trans = new Transaction(auth, clr);
		            log.info("AuthXClrSS transaction: " + trans);
		        	return trans;
		        }})
			.addSink(createSinkFromStaticConfig());
		
		env.execute("Flink Streaming Java API Skeleton");
    }    
}
