package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionXCard extends Transaction implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(TransactionXCard.class);

	private static final long serialVersionUID = 1L;
	protected Integer agreementId;
	protected String startDt;
	protected String finishDt;
	protected String cardNumber;
	private String processedDttm;

	public TransactionXCard() {

	}

	public TransactionXCard(Integer transactionId, Integer clearingId, Integer clearingTypeId, Integer authorizationId,
			Double clearingAmt, Integer cardId, String clearingDttm, String clearingTypeNm, Integer authorizationTypeId,
			Double authorizationAmt, String authorizationDttm, String authorizationTypeNm, Double transactionAmt,
			Integer agreementId, String startDt, String finishDt, String cardNumber, String authAwsDttm,
			String clrAwsDttm) {
		super(transactionId, clearingId, clearingTypeId, authorizationId, clearingAmt, cardId, clearingDttm,
				clearingTypeNm, authorizationTypeId, authorizationAmt, authorizationDttm, authorizationTypeNm,
				transactionAmt, authAwsDttm, clrAwsDttm);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
		this.cardNumber = cardNumber;
		this.authAwsDttm = authAwsDttm;
		this.clrAwsDttm = clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TransactionXCard(Transaction trn, Integer agreementId, String startDt, String finishDt, String cardNumber) {
		super(trn);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
		this.cardNumber = cardNumber;
		this.authAwsDttm = trn.authAwsDttm;
		this.clrAwsDttm = trn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TransactionXCard(Transaction trn, Card card) {
		super(trn);
		this.agreementId = card.agreementId;
		this.startDt = card.startDt;
		this.finishDt = card.finishDt;
		this.cardNumber = card.cardNumber;
		this.authAwsDttm = trn.authAwsDttm;
		this.clrAwsDttm = trn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TransactionXCard(String str) {
		this(str, ";");
	}

	public TransactionXCard(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.agreementId = Integer.valueOf(arr[16]);
			this.startDt = arr[17];
			this.finishDt = arr[18];
			this.cardNumber = arr[19];
			this.authAwsDttm = arr[20];
			this.clrAwsDttm = arr[21];
			this.processedDttm = arr[22];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public TransactionXCard(TransactionXCard trn) {
		super(trn);
		this.agreementId = trn.agreementId;
		this.startDt = trn.startDt;
		this.finishDt = trn.finishDt;
		this.cardNumber = trn.cardNumber;
		this.processedDttm = trn.processedDttm;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public String getStartDt() {
		return startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	public String getFinishDt() {
		return finishDt;
	}

	public void setFinishDt(String finishDt) {
		this.finishDt = finishDt;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@Override
	public String toString() {
		return super.toString().replace("\n", "") + ";" + agreementId + ";" + startDt + ";" + finishDt + ";"
				+ cardNumber + ";" + authAwsDttm + ";" + clrAwsDttm + ";" + processedDttm + "\n";
	}

	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

}
