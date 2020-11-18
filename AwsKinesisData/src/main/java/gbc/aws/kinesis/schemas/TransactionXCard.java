package gbc.aws.kinesis.schemas;

import java.io.Serializable;

public class TransactionXCard extends Transaction implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer agreementId;
	protected String startDt;
	protected String finishDt;
	protected String cardNumber;

	public TransactionXCard() {

	}

	public TransactionXCard(Integer transactionId, Integer clearingId, Integer clearingTypeId, Integer authorizationId,
			Double clearingAmt, Integer cardId, String clearingDttm, String clearingTypeNm, Integer authorizationTypeId,
			Double authorizationAmt, String authorizationDttm, String authorizationTypeNm, Double transactionAmt,
			Integer agreementId, String startDt, String finishDt, String cardNumber) {
		super(transactionId, clearingId, clearingTypeId, authorizationId, clearingAmt, cardId, clearingDttm,
				clearingTypeNm, authorizationTypeId, authorizationAmt, authorizationDttm, authorizationTypeNm,
				transactionAmt);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
		this.cardNumber = cardNumber;
	}

	public TransactionXCard(Transaction trn, Integer agreementId, String startDt, String finishDt, String cardNumber) {
		super(trn);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
		this.cardNumber = cardNumber;
	}

	public TransactionXCard(Transaction trn, Card card) {
		super(trn);
		this.agreementId = card.agreementId;
		this.startDt = card.startDt;
		this.finishDt = card.finishDt;
		this.cardNumber = card.cardNumber;
	}

	public TransactionXCard(TransactionXCard trn) {
		super(trn);
		this.agreementId = trn.agreementId;
		this.startDt = trn.startDt;
		this.finishDt = trn.finishDt;
		this.cardNumber = trn.cardNumber;
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
		return super.toString() + ", agreementId: " + agreementId + ", startDt: " + startDt + ", finishDt: " + finishDt
				+ ", cardNumber: " + cardNumber;
	}

}
