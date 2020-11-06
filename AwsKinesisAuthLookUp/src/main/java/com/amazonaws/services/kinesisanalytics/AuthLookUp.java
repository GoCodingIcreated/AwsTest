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

/**
 * A basic Kinesis Data Analytics for Java application with Kinesis data streams
 * as source and sink.
 */

public class AuthLookUp {
	private static final String region = "us-east-1";
	private static final String inputStreamName = "test_in_auth_ss";
	private static final String outputStreamName = "AUTH_FILTERED";
	private static final Logger log = LoggerFactory.getLogger(AuthLookUp.class);

	private static DataStream<String> createSourceFromStaticConfig(StreamExecutionEnvironment env) {
		Properties inputProperties = new Properties();
		inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

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
			AuthorizationWithType rec = new AuthorizationWithType(value); 
			log.info("Got value: " + value + ", transformed to: " + rec);
			return rec;
		});
		/*
		 * if you would like to use runtime configuration properties, uncomment the
		 * lines below input.addSink(createSinkFromApplicationProperties())
		 */
		auth.addSink(createSinkFromStaticConfig());

		env.execute("Authorization with LookUp v.1.0.0.");
	}
}
