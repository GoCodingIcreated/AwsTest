package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authorization implements Serializable {
	protected static final Logger log = LoggerFactory.getLogger(Authorization.class);
	protected static final long serialVersionUID = 1L;
	protected Integer authorizationId;
	protected Integer authorizationTypeId;
	protected Double authorizationAmt;
	protected Integer cardId;
	protected String authorizationDttm;

	public Integer getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(Integer authorizationId) {
		this.authorizationId = authorizationId;
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

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public String getAuthorizationDttm() {
		return authorizationDttm;
	}

	public void setAuthorizationDttm(String authorizationDttm) {
		this.authorizationDttm = authorizationDttm;
	}

	public Authorization() {

	}

	public Authorization(Authorization auth) {
		this.authorizationId = auth.authorizationId;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.cardId = auth.cardId;
		this.authorizationDttm = auth.authorizationDttm;
	}

	public Authorization(String str) {
		this(str, ";");
	}

	public Authorization(String str, String cep) {
		String arr[] = str.split(cep);
		try {
			this.authorizationId = Integer.valueOf(arr[0]);
			this.authorizationTypeId = Integer.valueOf(arr[1]);
			this.authorizationAmt = Double.valueOf(arr[2]);
			this.cardId = Integer.valueOf(arr[3]);
			this.authorizationDttm = arr[4];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public Authorization(Integer authorizationId, Integer authorizationTypeId, Double authorizationAmt, Integer cardId,
			String authorizationDttm) {
		super();
		this.authorizationId = authorizationId;
		this.authorizationTypeId = authorizationTypeId;
		this.authorizationAmt = authorizationAmt;
		this.cardId = cardId;
		this.authorizationDttm = authorizationDttm;
	}

	@Override
	public String toString() {
		return "authorizationId: " + authorizationId + ", authorizationTypeId: " + authorizationTypeId
				+ ", authorizationAmt: " + authorizationAmt + ", cardId: " + cardId + ", authorizationDttm: "
				+ authorizationDttm;
	}

}
