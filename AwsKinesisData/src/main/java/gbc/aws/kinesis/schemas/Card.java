package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "CARD")
public class Card implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer cardId;
	protected Integer agreementId;
	protected String startDt;
	protected String finishDt;
	protected String cardNumber;

	public Card() {
		super();
	}

	public Card(Card clrType) {
		super();
		this.cardId = clrType.cardId;
		this.agreementId = clrType.agreementId;
		this.startDt = clrType.startDt;
		this.finishDt = clrType.finishDt;
		this.cardNumber = clrType.cardNumber;
	}

	public Card(Integer cardId, Integer agreementId, String startDt, String finishDt, String cardNumber) {
		super();
		this.cardId = cardId;
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
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

	@DynamoDBAttribute(attributeName = "START_DT")
	public String getStartDt() {
		return startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	@DynamoDBAttribute(attributeName = "FINISH_DT")
	public String getFinishDt() {
		return finishDt;
	}

	public void setFinishDt(String finishDt) {
		this.finishDt = finishDt;
	}

	@DynamoDBAttribute(attributeName = "CARD_NUMBER")
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Override
	public String toString() {
		return "cardId:  " + cardId + ", agreementId:  " + agreementId + ", startDt:  " + startDt + ", finishDt:  "
				+ finishDt + ", cardNumber:  " + cardNumber;
	}

}
