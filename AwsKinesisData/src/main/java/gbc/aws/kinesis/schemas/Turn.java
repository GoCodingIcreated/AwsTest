package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName = "TURN")
public class Turn implements Serializable {
	protected static final Logger log = LoggerFactory.getLogger(Turn.class);
	private static final long serialVersionUID = 1L;
	protected String cardNumber;
	protected Integer cardId;
	protected Integer agreementId;
	protected String cardStartDt;
	protected String cardFinishDt;
	protected Double turnAmt;
	protected String monthDt;
	protected String authAwsDttm;
	protected String clrAwsDttm;
	private String processedDttm;

	public Turn() {
		super();
	}

	public Turn(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String cardFinishDt,
			Double turnAmt, String monthDt, String authAwsDttm, String clrAwsDttm) {
		super();
		this.cardNumber = cardNumber;
		this.cardId = cardId;
		this.agreementId = agreementId;
		this.cardStartDt = cardStartDt;
		this.cardFinishDt = cardFinishDt;
		this.turnAmt = turnAmt;
		this.monthDt = monthDt;
		this.authAwsDttm = authAwsDttm;
		this.clrAwsDttm = clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public Turn(TransactionXCard trn, Double turnAmt, String monthDt) {
		super();
		this.cardNumber = trn.cardNumber;
		this.cardId = trn.cardId;
		this.agreementId = trn.agreementId;
		this.cardStartDt = trn.startDt;
		this.cardFinishDt = trn.finishDt;
		this.turnAmt = turnAmt;
		this.monthDt = monthDt;
		this.authAwsDttm = trn.authAwsDttm;
		this.clrAwsDttm = trn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public Turn(Turn turn) {
		super();
		this.cardNumber = turn.cardNumber;
		this.cardId = turn.cardId;
		this.agreementId = turn.agreementId;
		this.cardStartDt = turn.cardStartDt;
		this.cardFinishDt = turn.cardFinishDt;
		this.turnAmt = turn.turnAmt;
		this.monthDt = turn.monthDt;
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = turn.processedDttm;
	}

	public Turn(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.cardNumber = arr[0];
			this.cardId = Integer.valueOf(arr[1]);
			this.agreementId = Integer.valueOf(arr[2]);
			this.cardStartDt = arr[3];
			this.cardFinishDt = arr[4];
			this.turnAmt = Double.valueOf(arr[5]);
			this.monthDt = arr[6];
			this.authAwsDttm = arr[7];
			this.clrAwsDttm = arr[8];
			this.processedDttm = arr[9];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public Turn(String str) {
		this(str, ";");
	}

	public Turn(Turn turn, TransactionXCard trn) {
		this(turn);
		this.turnAmt += trn.transactionAmt;
		this.authAwsDttm = trn.authAwsDttm;
		this.clrAwsDttm = trn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	@DynamoDBAttribute(attributeName = "CARD_NUMBER")
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@DynamoDBHashKey(attributeName = "CARD_ID")
	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	@DynamoDBAttribute(attributeName = "AGREEMENT_ID")
	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	@DynamoDBAttribute(attributeName = "CARD_START_DT")
	public String getCardStartDt() {
		return cardStartDt;
	}

	public void setCardStartDt(String startDt) {
		this.cardStartDt = startDt;
	}

	@DynamoDBAttribute(attributeName = "CARD_FINISH_DT")
	public String getcardFinishDt() {
		return cardFinishDt;
	}

	public void setcardFinishDt(String finishDt) {
		this.cardFinishDt = finishDt;
	}

	@DynamoDBAttribute(attributeName = "TURN_AMT")
	public Double getTurnAmt() {
		return turnAmt;
	}

	public void setTurnAmt(Double turnAmt) {
		this.turnAmt = turnAmt;
	}

	@DynamoDBAttribute(attributeName = "MONTH_DT")
	public String getMonthDt() {
		return monthDt;
	}

	public void setMonthDt(String monthDt) {
		this.monthDt = monthDt;
	}

	@Override
	public String toString() {
		return cardNumber + ";" + cardId + ";" + agreementId + ";" + cardStartDt + ";" + cardFinishDt + ";" + turnAmt
				+ ";" + monthDt + ";" + authAwsDttm + ";" + clrAwsDttm + ";" + processedDttm + "\n";
	}

	@DynamoDBAttribute(attributeName = "PROCESSED_DTTM")
	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}


	@DynamoDBAttribute(attributeName = "AUTH_AWS_DTTM")
	public String getAuthAwsDttm() {
		return authAwsDttm;
	}

	public void setAuthAwsDttm(String authAwsDttm) {
		this.authAwsDttm = authAwsDttm;
	}

	@DynamoDBAttribute(attributeName = "CLR_AWS_DTTM")
	public String getClrAwsDttm() {
		return clrAwsDttm;
	}

	public void setClrAwsDttm(String clrAwsDttm) {
		this.clrAwsDttm = clrAwsDttm;
	}
}
