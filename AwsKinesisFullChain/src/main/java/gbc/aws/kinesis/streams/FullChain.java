package gbc.aws.kinesis.streams;

import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import gbc.aws.kinesis.schemas.Agreement;
import gbc.aws.kinesis.schemas.Authorization;
import gbc.aws.kinesis.schemas.AuthorizationType;
import gbc.aws.kinesis.schemas.AuthorizationXType;
import gbc.aws.kinesis.schemas.AwsKinesisData;
import gbc.aws.kinesis.schemas.Bucket;
import gbc.aws.kinesis.schemas.BucketXCustomer;
import gbc.aws.kinesis.schemas.Card;
import gbc.aws.kinesis.schemas.Clearing;
import gbc.aws.kinesis.schemas.ClearingType;
import gbc.aws.kinesis.schemas.ClearingXType;
import gbc.aws.kinesis.schemas.Customer;
import gbc.aws.kinesis.schemas.Product;
import gbc.aws.kinesis.schemas.Transaction;
import gbc.aws.kinesis.schemas.TransactionXCard;
import gbc.aws.kinesis.schemas.Turn;
import gbc.aws.kinesis.schemas.TurnXAgr;
import gbc.aws.kinesis.schemas.TurnXAgrXProd;

public class FullChain {

	private static final Logger log = LoggerFactory.getLogger(FullChain.class);

	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	private static final String region = "us-east-1";
	private static final String inputStreamName1 = "AUTH_X_TYPE";
	private static final String inputStreamName2 = "CLR_X_TYPE";
	private static final String outputStreamName = "TRANSACTION";
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

	private static FlinkKinesisProducer<String> createSinkFromStaticConfig() {
		Properties outputProperties = new Properties();
		outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, aws_access_key_id);
		outputProperties.setProperty(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, aws_secret_access_key);
		outputProperties.setProperty("AggregationEnabled", "true");

		FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<String>(new SimpleStringSchema(),
				outputProperties);
		sink.setDefaultStream(outputStreamName);
		sink.setDefaultPartition("0");
		return sink;
	}

	public static DataStream<AuthorizationXType> step1a(DataStream<String> input) {
		DataStream<AuthorizationXType> auth = input.flatMap(new FlatMapFunction<String, AuthorizationXType>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void flatMap(String value, Collector<AuthorizationXType> out) throws Exception {
				log.info("Map_1: Got value: " + value);
				Authorization authRec = new Authorization(value, ";", true);
				try {

					DynamoDBMapper mapper = new DynamoDBMapper(client);
					AuthorizationType authType = mapper.load(AuthorizationType.class, authRec.getAuthorizationTypeId());
					AuthorizationXType authWithType = new AuthorizationXType(authRec,
							authType.getAuthorizationTypeNm());

					log.info("Map_1: Collected value: " + value + ", authRec: " + authRec + ", authType: " + authType
							+ ", authWithType: " + authWithType + ", AuthorizationTypeNm: "
							+ authWithType.getAuthorizationTypeNm());
					out.collect(authWithType);
				} catch (Exception ex) {
					log.error("Map_1 exception: ", ex);
					AuthorizationXType authWithType = new AuthorizationXType(authRec, new AuthorizationType());
					log.error("Map_1: value: " + value + ", authRec: " + authRec + ", authWithType: " + authWithType);
					out.collect(authWithType);
				}
			}
		});

		return auth;
	}

	public static DataStream<ClearingXType> step1b(DataStream<String> input) {
		DataStream<ClearingXType> result = input.map(new MapFunction<String, ClearingXType>() {
			private static final long serialVersionUID = 1L;

			@Override
			public ClearingXType map(String value) throws Exception {
				Clearing clrRec = new Clearing(value, ";", true);
				DynamoDBMapper mapper = new DynamoDBMapper(client);
				try {
					ClearingType clrType = mapper.load(ClearingType.class, clrRec.getClearingTypeId());
					ClearingXType clrWithType = new ClearingXType(clrRec, clrType.getClearingTypeNm());
					log.info("Map 1: Value: " + value + ", clrRec: " + clrRec + ", clrType: " + clrType
							+ ", clrWithType: " + clrWithType);
					return clrWithType;
				} catch (Exception ex) {
					log.error("Map_1 exception: ", ex);
					ClearingXType clrWithType = new ClearingXType(clrRec, "");
					log.error("Map 1: Value: " + value + ", clrRec: " + clrRec + ", clrType: null, clrWithType: "
							+ clrWithType);
					return clrWithType;
				}

			};
		});
		return result;
	}

	public static DataStream<Transaction> step2(DataStream<AuthorizationXType> auth, DataStream<ClearingXType> clr) {
		return null;
	}

	public static DataStream<TransactionXCard> step3(DataStream<Transaction> input) {
		DataStream<TransactionXCard> trans = input.map(new MapFunction<Transaction, TransactionXCard>() {
			private static final long serialVersionUID = 1L;

			@Override
			public TransactionXCard map(Transaction trn) throws Exception {
				try {
					DynamoDBMapper mapper = new DynamoDBMapper(client);
					// Transaction trn = new Transaction(trnStr);
					Card card = mapper.load(Card.class, trn.getCardId());			
					TransactionXCard trnXCard = new TransactionXCard(trn, card);
					log.info("Map_1: trn: " + trn + ", card: " + card + ", transXCard: " + trnXCard);
					return trnXCard;
				} 
				catch (Exception ex) {
					log.error("Map_1 exception: ", ex);
					TransactionXCard trnXCard = new TransactionXCard(trn, new Card()); 
					log.error("Map_1: trn: " + trn + ", trnXCard: " + trnXCard + ", card: null");
					return trnXCard;
				}
			}
		});
		return trans;
	}

	public static DataStream<Turn> step4(DataStream<TransactionXCard> input) {
		return null;
	}

	public static DataStream<TurnXAgr> step5(DataStream<Turn> input) {
		DataStream<TurnXAgr> turnXAgreement = input.map((turn) -> {
			//Turn turn = new Turn(turnStr);
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			try {
				Agreement agr = mapper.load(Agreement.class, turn.getAgreementId());
				TurnXAgr turnXAgr = new TurnXAgr(turn, agr);
				log.info("Map 1: turn: " + turn + ", agr: " + agr + ", turnXAgr: " + turnXAgr);
				return turnXAgr;
			}
			catch(Exception ex) {
				log.error("Map_1: exception: ", ex);
				Agreement agr = new Agreement();
				TurnXAgr turnXAgr = new TurnXAgr(turn, agr);
				log.error("Map 1: turn: " + turn + ", agr: " + agr + ", turnXAgr: " + turnXAgr);
				return turnXAgr;
			}
			

		});
		return turnXAgreement;
	}

	public static DataStream<TurnXAgrXProd> step6(DataStream<TurnXAgr> input) {
		DataStream<TurnXAgrXProd> turnXAgreementXProduct = input.map((turnXAgr) -> {
			// TurnXAgr turnXAgr = new TurnXAgr(turnXAgrStr);
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			try {
				Product prod = mapper.load(Product.class, turnXAgr.getAgreementId());
				TurnXAgrXProd turnXAgrXProd = new TurnXAgrXProd(turnXAgr, prod);
				log.info("Map 1: turnXAgr: " + turnXAgr + ", prod: " + prod + ", turnXAgrXProd: " + turnXAgrXProd);
				return turnXAgrXProd;
			}
			catch(Exception ex) {
				log.error("Map_1 exception: ", ex);
				Product prod = new Product();
				TurnXAgrXProd turnXAgrXProd = new TurnXAgrXProd(turnXAgr, prod);
				log.error("Map 1: turnXAgr: " + turnXAgr + ", prod: " + prod + ", turnXAgrXProd: " + turnXAgrXProd);
				return turnXAgrXProd;
			}
		});
		return turnXAgreementXProduct;
	}

	public static DataStream<Bucket> step7(DataStream<TurnXAgrXProd> input) {
		return null;
	}

	public static DataStream<String> step8(DataStream<Bucket> input) {
		DataStream<String> bucketXCustomer = input.map((bucket) -> {
			// Bucket bucket = new Bucket(bucketStr);
			
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

		return bucketXCustomer;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> auth = createSource1FromStaticConfig(env);
		DataStream<String> clr = createSource2FromStaticConfig(env);
		DataStream<AuthorizationXType> authXType = step1a(auth);
		DataStream<ClearingXType> clrXType = step1b(clr);
		DataStream<Transaction> trn = step2(authXType, clrXType);
		DataStream<TransactionXCard> trnXCard = step3(trn);
		DataStream<Turn> turn = step4(trnXCard);
		DataStream<TurnXAgr> turnXAgr = step5(turn);
		DataStream<TurnXAgrXProd> turnXAgrXProd = step6(turnXAgr);
		DataStream<Bucket> bucket = step7(turnXAgrXProd);
		DataStream<String> bucketXCustomer = step8(bucket);
		bucketXCustomer.addSink(createSinkFromStaticConfig());
		env.execute("FullChain v.1.0.0.");
	}

}
