package gbc.aws.kinesis.schemas;

import java.io.Serializable;

public class Transaction  implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer transactionId;
	protected Integer clearingId;
	protected Integer clearingTypeId;
	protected Integer authorizationId;
	protected Double clearingAmt;
	protected Integer cardId;
	protected String clearingDttm;
	protected String clearingTypeNm;
	protected Integer authorizationTypeId;
	protected Double authorizationAmt;
	protected String authorizationDttm;
	protected String authorizationTypeNm;
	protected Double transactionAmt;

	public Transaction() {
		super();
	}

	public Transaction(Integer transactionId, Integer clearingId, Integer clearingTypeId, Integer authorizationId, Double clearingAmt,
			Integer cardId, String clearingDttm, String clearingTypeNm, Integer authorizationTypeId, Double authorizationAmt,
			String authorizationDttm, String authorizationTypeNm, Double transactionAmt) {
		super();
		this.transactionId = transactionId;
		this.clearingId = clearingId;
		this.clearingTypeId = clearingTypeId;
		this.authorizationId = authorizationId;
		this.clearingAmt = clearingAmt;
		this.cardId = cardId;
		this.clearingDttm = clearingDttm;
		this.clearingTypeNm = clearingTypeNm;
		this.authorizationTypeId = authorizationTypeId;
		this.authorizationAmt = authorizationAmt;
		this.authorizationDttm = authorizationDttm;
		this.authorizationTypeNm = authorizationTypeNm;
		this.transactionAmt = transactionAmt;
	}

	public Transaction(AuthorizationXType auth) {
		this.transactionId = auth.authorizationId;			
		this.authorizationId = auth.authorizationId;		
		this.cardId = auth.cardId;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.authorizationDttm = auth.authorizationDttm;
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.transactionAmt = auth.authorizationAmt;
	}

	public Transaction(ClearingXType clr) {
		this.transactionId = clr.clearingId;
		this.clearingId = clr.clearingId;
		this.clearingTypeId = clr.clearingTypeId;
		this.authorizationId = clr.authorizationId;
		this.clearingAmt = clr.clearingAmt;
		this.cardId = clr.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.clearingTypeNm = clr.clearingTypeNm;
		this.transactionAmt = clr.clearingAmt;
	}

	public Transaction(AuthorizationXType auth, ClearingXType clr) {	
		this.transactionId = clr.clearingId;
		this.clearingId = clr.clearingId;
		this.clearingTypeId = clr.clearingTypeId;	
		this.authorizationId = auth.authorizationId;		
		this.clearingAmt = clr.clearingAmt;		
		this.cardId = clr.cardId != null ? clr.cardId : auth.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.clearingTypeNm = clr.clearingTypeNm;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.authorizationDttm = auth.authorizationDttm;
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.transactionAmt = clr.clearingAmt;
	}

	public Transaction(Transaction trn) {
		this.transactionId = trn.clearingId;
		this.clearingId = trn.clearingId;
		this.clearingTypeId = trn.clearingTypeId;	
		this.authorizationId = trn.authorizationId;		
		this.clearingAmt = trn.clearingAmt;		
		this.cardId = trn.cardId;
		this.clearingDttm = trn.clearingDttm;
		this.clearingTypeNm = trn.clearingTypeNm;
		this.authorizationTypeId = trn.authorizationTypeId;
		this.authorizationAmt = trn.authorizationAmt;
		this.authorizationDttm = trn.authorizationDttm;
		this.authorizationTypeNm = trn.authorizationTypeNm;
		this.transactionAmt = trn.clearingAmt;
	}

	public Transaction(Transaction trn, AuthorizationXType auth) {
		this.transactionId = trn.clearingId;
		this.clearingId = trn.clearingId;
		this.clearingTypeId = trn.clearingTypeId;	
		this.authorizationId = auth.authorizationId;		
		this.clearingAmt = trn.clearingAmt;		
		this.cardId = trn.cardId != null ? trn.cardId : auth.cardId;
		this.clearingDttm = trn.clearingDttm;
		this.clearingTypeNm = trn.clearingTypeNm;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.authorizationDttm = auth.authorizationDttm;
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.transactionAmt = trn.clearingAmt;
	}

	public Transaction(Transaction trn, ClearingXType clr) {
		this.transactionId = clr.clearingId;
		this.clearingId = clr.clearingId;
		this.clearingTypeId = clr.clearingTypeId;	
		this.authorizationId = trn.authorizationId;		
		this.clearingAmt = clr.clearingAmt;		
		this.cardId = clr.cardId != null ? clr.cardId : trn.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.clearingTypeNm = clr.clearingTypeNm;
		this.authorizationTypeId = trn.authorizationTypeId;
		this.authorizationAmt = trn.authorizationAmt;
		this.authorizationDttm = trn.authorizationDttm;
		this.authorizationTypeNm = trn.authorizationTypeNm;
		this.transactionAmt = clr.clearingAmt;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getClearingId() {
		return clearingId;
	}

	public void setClearingId(Integer clearingId) {
		this.clearingId = clearingId;
	}

	public Integer getClearingTypeId() {
		return clearingTypeId;
	}

	public void setClearingTypeId(Integer clearingTypeId) {
		this.clearingTypeId = clearingTypeId;
	}

	public Integer getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(Integer authorizationId) {
		this.authorizationId = authorizationId;
	}

	public Double getClearingAmt() {
		return clearingAmt;
	}

	public void setClearingAmt(Double clearingAmt) {
		this.clearingAmt = clearingAmt;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public String getClearingDttm() {
		return clearingDttm;
	}

	public void setClearingDttm(String clearingDttm) {
		this.clearingDttm = clearingDttm;
	}

	public String getClearingTypeNm() {
		return clearingTypeNm;
	}

	public void setClearingTypeNm(String clearingTypeNm) {
		this.clearingTypeNm = clearingTypeNm;
	}

	public Integer getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	public void setAuthorizationTypeId(Integer authorizationTypeId) {
		this.authorizationTypeId = authorizationTypeId;
	}

	public Double getAuthorizationAmt() {
		return authorizationAmt;
	}

	public void setAuthorizationAmt(Double authorizationAmt) {
		this.authorizationAmt = authorizationAmt;
	}

	public String getAuthorizationDttm() {
		return authorizationDttm;
	}

	public void setAuthorizationDttm(String authorizationDttm) {
		this.authorizationDttm = authorizationDttm;
	}

	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}

	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public Double getTransactionAmt() {
		return transactionAmt;
	}

	public void setTransactionAmt(Double transactionAmt) {
		this.transactionAmt = transactionAmt;
	}

	@Override
	public String toString() {
		return "transactionId: " + transactionId + ", clearingId: " + clearingId + ", clearingTypeId: " + clearingTypeId
				+ ", authorizationId: " + authorizationId + ", clearingAmt: " + clearingAmt + ", cardId: " + cardId
				+ ", clearingDttm: " + clearingDttm + ", clearingTypeNm: " + clearingTypeNm + ", authorizationTypeId: "
				+ authorizationTypeId + ", authorizationAmt: " + authorizationAmt + ", authorizationDttm: "
				+ authorizationDttm + ", authorizationTypeNm: " + authorizationTypeNm + ", transactionAmt: "
				+ transactionAmt;
	}

}
