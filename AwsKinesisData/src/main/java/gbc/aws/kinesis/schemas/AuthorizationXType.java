package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AUTH_X_TYPE")
public class AuthorizationXType extends Authorization implements Serializable {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(AuthorizationXType.class);
	protected String authorizationTypeNm;	
	private String processedDttm;

	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}

	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public AuthorizationXType() {

	}

	public AuthorizationXType(String str) {
		this(str, ";");
	}

	public AuthorizationXType(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.authorizationTypeNm = arr[7];
			this.awsDttm = arr[8];
			this.processedDttm = arr[9];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public AuthorizationXType(Integer authorizationId, Integer authorizationTypeId, Double authorizationAmt,
			Integer cardId, String authorizationDttm, String authorizationTypeNm, String awsDttm) {
		super(authorizationId, authorizationTypeId, authorizationAmt, cardId, authorizationDttm);
		this.authorizationTypeNm = authorizationTypeNm;
		this.awsDttm = awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public AuthorizationXType(Authorization auth, String authorizationTypeNm) {
		super(auth);
		this.authorizationTypeNm = authorizationTypeNm;
		this.awsDttm = auth.awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public AuthorizationXType(AuthorizationXType auth) {
		super(auth);
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.processedDttm = auth.processedDttm;
		this.awsDttm = auth.awsDttm;
	}

	public AuthorizationXType(AuthorizationXType auth, AuthorizationType authType) {
		super(auth);
		this.authorizationTypeNm = authType.authorizationTypeNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
		this.awsDttm = auth.awsDttm;
	}
	
	@Override
	public String toString() {
		return super.toString().replace("\n", "") + ";" + authorizationTypeNm +  ";" + awsDttm + ";" + processedDttm + "\n";
	}

	@Override
	@DynamoDBHashKey(attributeName = "AUTHORIZATION_ID")
	public Integer getAuthorizationId() {
		return authorizationId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_TYPE_ID")
	public Integer getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_AMT")
	public Double getAuthorizationAmt() {
		return authorizationAmt;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CARD_ID")
	public Integer getCardId() {
		return cardId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_DTTM")
	public String getAuthorizationDttm() {
		return authorizationDttm;
	}

	@Override
	@DynamoDBAttribute(attributeName = "PROCESSED_DTTM")
	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

}
