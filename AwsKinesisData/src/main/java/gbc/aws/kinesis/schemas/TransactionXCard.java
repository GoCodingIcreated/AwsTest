package gbc.aws.kinesis.schemas;

public class TransactionXCard extends Transaction {
	protected Integer agreementId;
	protected String startDt;
	protected String finishDt;

	public TransactionXCard() {

	}

	public TransactionXCard(Integer transactionId, Integer clearingId, Integer clearingTypeId, Integer authorizationId,
			Double clearingAmt, Integer cardId, String clearingDttm, String clearingTypeNm, Integer authorizationTypeId,
			Double authorizationAmt, String authorizationDttm, String authorizationTypeNm, Double transactionAmt,
			Integer agreementId, String startDt, String finishDt) {
		super(transactionId, clearingId, clearingTypeId, authorizationId, clearingAmt, cardId, clearingDttm,
				clearingTypeNm, authorizationTypeId, authorizationAmt, authorizationDttm, authorizationTypeNm,
				transactionAmt);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
	}
	
	public TransactionXCard(Transaction trn, Integer agreementId, String startDt, String finishDt) {
		super(trn);
		this.agreementId = agreementId;
		this.startDt = startDt;
		this.finishDt = finishDt;
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

}
