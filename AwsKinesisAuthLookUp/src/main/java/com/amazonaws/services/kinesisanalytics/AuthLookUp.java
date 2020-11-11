package com.amazonaws.services.kinesisanalytics;

import com.amazonaws.services.kinesisanalytics.model.KinesisStreamsInput;
import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.serialization.KinesisSerializationSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic Kinesis Data Analytics for Java application with Kinesis data streams
 * as source and sink.
 */

public class AuthLookUp {
    private static final String region = "us-east-1";
    private static final String inputStreamName = "test_in_auth_ss";
    private static final String outputStreamName = "AUTH_FILTERED";
    private static final Logger log = LoggerFactory.getLogger(AuthLookUp.class);
    
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private static final String authorizationTypeTableName = "";
    

    private static final String aws_access_key_id = "";
    private static final String aws_secret_access_key = "";

    private static DataStream<String> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
        Properties inputProperties = new Properties();
        inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
        inputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
        inputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);

        return env.addSource(new FlinkKinesisConsumer<>(inputStreamName, new SimpleStringSchema(), inputProperties));
    }

    private static FlinkKinesisProducer<AuthorizationWithType> createSinkFromStaticConfig() {
        Properties outputProperties = new Properties();
        outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        outputProperties.setProperty("AggregationEnabled", "false");
        FlinkKinesisProducer<AuthorizationWithType> sink = new FlinkKinesisProducer<AuthorizationWithType>(
                new ProjectSchema<>(AuthorizationWithType.class), outputProperties);
        sink.setDefaultStream(outputStreamName);
        sink.setDefaultPartition("0");
        new KinesisStreamsInput();
        return sink;
    }

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /*
         * if you would like to use runtime configuration properties, uncomment the
         * lines below DataStream<String> input =
         * createSourceFromApplicationProperties(env);
         */
        DataStream<String> input = createSourceFromStaticConfig(env);
        DataStream<AuthorizationWithType> auth = input.map((value) -> {        	
            Authorization authRec = new Authorization(value);            
            DynamoDBMapper mapper = new DynamoDBMapper(client);            
            AuthorizationType authType = mapper.load(AuthorizationType.class, authRec.getAuthorizationTypeId());            
            AuthorizationWithType authWithType = new AuthorizationWithType(authRec, authType.getAuthorizationTypeNm());
            log.info("Map 1: value: " + value + ", authRec: " + authRec + ", authType: " + authType + ", authWithType: " + authWithType);                        
            return authWithType;
        });
        /*
         * if you would like to use runtime configuration properties, uncomment the
         * lines below input.addSink(createSinkFromApplicationProperties())
         */
        auth.addSink(createSinkFromStaticConfig());

        env.execute("Authorization with LookUp v.1.0.7.");
    }
}
