package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "CLR_X_TYPE")
public class ClearingXType extends Clearing implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(ClearingXType.class);
	private static final long serialVersionUID = 1L;
	protected String clearingTypeNm;
	private String processedDttm;

	public ClearingXType() {
		super();
	}

	public ClearingXType(Integer clearingId, Integer clearingTypeCd, Integer authorizationId, Double clearingAmt,
			Integer cardId, String clearingDttm, String clearingTypeNm, String awsDttm) {
		super(clearingId, clearingTypeCd, authorizationId, clearingAmt, cardId, clearingDttm, awsDttm);
		this.clearingTypeNm = clearingTypeNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public ClearingXType(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.clearingTypeNm = arr[8];
			this.awsDttm = arr[9];
			this.processedDttm = arr[10];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public ClearingXType(String str) {
		this(str, ";");
	}

	public ClearingXType(ClearingXType clr) {
		super(clr);
		this.clearingTypeNm = clr.clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = clr.processedDttm;
	}

	public ClearingXType(Clearing clr, String clearingTypeNm) {
		super(clr);
		this.clearingTypeNm = clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public ClearingXType(Clearing clr, ClearingType clrType) {
		super(clr);
		this.clearingTypeNm = clrType.clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	@DynamoDBAttribute(attributeName = "CLEARING_TYPE_NM")
	public String getClearingTypeNm() {
		return clearingTypeNm;
	}

	public void setClearingTypeNm(String clearingTypeNm) {
		this.clearingTypeNm = clearingTypeNm;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CLEARING_ID")
	public Integer getClearingId() {
		return clearingId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CLEARING_TYPE_ID")
	public Integer getClearingTypeId() {
		return clearingTypeId;
	}

	@Override
	@DynamoDBHashKey(attributeName = "AUTHORIZATION_ID")
	public Integer getAuthorizationId() {
		return authorizationId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CLEARING_AMT")
	public Double getClearingAmt() {
		return clearingAmt;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CARD_ID")
	public Integer getCardId() {
		return cardId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CLEARING_DTTM")
	public String getClearingDttm() {
		return clearingDttm;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AWS_DTTM")
	public String getAwsDttm() {
		return awsDttm;
	}

	@Override
	@DynamoDBAttribute(attributeName = "PROCESSED_DTTM")
	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

	@Override
	public String toString() {
		return super.toString().replace("\n", "") + ";" + clearingTypeNm + ";" + awsDttm + ";" + processedDttm + "\n";
	}

}
