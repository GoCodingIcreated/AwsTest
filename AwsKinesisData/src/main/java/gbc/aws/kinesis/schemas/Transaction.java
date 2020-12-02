package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction  implements Serializable {
	private static Logger log = LoggerFactory.getLogger(Transaction.class);
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
		this.authorizationId = clr.authorizationId != null ? clr.authorizationId : auth.authorizationId;		
		this.clearingAmt = clr.clearingAmt;		
		this.cardId = clr.cardId != null ? clr.cardId : auth.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.clearingTypeNm = clr.clearingTypeNm;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.authorizationDttm = auth.authorizationDttm;
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.transactionAmt = clr.clearingAmt != null ? clr.clearingAmt : auth.authorizationAmt;
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
		this.transactionAmt = trn.transactionAmt;
	}

	public Transaction(Transaction trn, AuthorizationXType auth) {
		this.transactionId = trn.clearingId;
		this.clearingId = trn.clearingId;
		this.clearingTypeId = trn.clearingTypeId;	
		this.authorizationId = trn.authorizationId != null ? trn.authorizationId : auth.authorizationId;		
		this.clearingAmt = trn.clearingAmt;		
		this.cardId = trn.cardId != null ? trn.cardId : auth.cardId;
		this.clearingDttm = trn.clearingDttm;
		this.clearingTypeNm = trn.clearingTypeNm;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.authorizationDttm = auth.authorizationDttm;
		this.authorizationTypeNm = auth.authorizationTypeNm;
		this.transactionAmt = trn.transactionAmt != null ? trn.transactionAmt : auth.authorizationAmt;
	}

	public Transaction(Transaction trn, ClearingXType clr) {
		this.transactionId = clr.clearingId;
		this.clearingId = clr.clearingId;
		this.clearingTypeId = clr.clearingTypeId;	
		this.authorizationId = clr.authorizationId != null ? clr.authorizationId : trn.authorizationId;;		
		this.clearingAmt = clr.clearingAmt;		
		this.cardId = clr.cardId != null ? clr.cardId : trn.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.clearingTypeNm = clr.clearingTypeNm;
		this.authorizationTypeId = trn.authorizationTypeId;
		this.authorizationAmt = trn.authorizationAmt;
		this.authorizationDttm = trn.authorizationDttm;
		this.authorizationTypeNm = trn.authorizationTypeNm;
		this.transactionAmt = clr.clearingAmt != null ? clr.clearingAmt : trn.transactionAmt;
	}

	public Transaction(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			if (!arr[0].equals("null")) {
				this.transactionId = Integer.valueOf(arr[0]);
			}
			if (!arr[1].equals("null")) {
				this.clearingId = Integer.valueOf(arr[1]);
			}
			if (!arr[2].equals("null")) {
				this.clearingTypeId = Integer.valueOf(arr[2]);	
			}
			if (!arr[3].equals("null")) {
				this.authorizationId = Integer.valueOf(arr[3]);	
			}
			if (!arr[4].equals("null")) {
				this.clearingAmt = Double.valueOf(arr[4]);
			}
			if (!arr[5].equals("null")) {
				this.cardId = Integer.valueOf(arr[5]);
			}
			if (!arr[6].equals("null")) {
				this.clearingDttm = arr[6];
			}
			if (!arr[7].equals("null")) {
				this.clearingTypeNm = arr[7];	
			}
			if (!arr[8].equals("null")) {
				this.authorizationTypeId = Integer.valueOf(arr[8]);
			}
			if (!arr[9].equals("null")) {
				this.authorizationAmt = Double.valueOf(arr[9]);
			}
			if (!arr[10].equals("null")) {
				this.authorizationDttm = arr[10];
			}
			if (!arr[11].equals("null")) {
				this.authorizationTypeNm = arr[11];
			}
			if (!arr[12].equals("null")) {
				this.transactionAmt = Double.valueOf(arr[12]);
			}
	
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
		
	}
	
	public Transaction(String str) {
		this(str, ";");
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
		return transactionId + ";" + clearingId + ";" + clearingTypeId + ";" + authorizationId + ";" + clearingAmt + ";"
				+ cardId + ";" + clearingDttm + ";" + clearingTypeNm + ";" + authorizationTypeId + ";"
				+ authorizationAmt + ";" + authorizationDttm + ";" + authorizationTypeNm + ";" + transactionAmt + "\n";
	}

}
