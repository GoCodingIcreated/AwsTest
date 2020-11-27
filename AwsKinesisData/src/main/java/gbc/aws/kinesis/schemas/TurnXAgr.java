package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurnXAgr extends Turn implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(TurnXAgr.class);
	private static final long serialVersionUID = 1L;
	protected Integer customerId;
	protected Integer productId;
	protected String agreementNumber;
	protected String agrStartDt;
	protected String plannedFinishDt;
	protected String factFinishDt;

	public TurnXAgr() {

	}

	public TurnXAgr(String cardNumber, Integer cardId, Integer agreementId, String startDt, String finishDt,
			Double turnAmt, String monthDt) {
		super(cardNumber, cardId, agreementId, startDt, finishDt, turnAmt, monthDt);

	}

	public TurnXAgr(TurnXAgr turn) {
		super(turn);
		this.customerId = turn.customerId;
		this.productId = turn.productId;
		this.agreementNumber = turn.agreementNumber;
		this.agrStartDt = turn.agrStartDt;
		this.plannedFinishDt = turn.plannedFinishDt;
		this.factFinishDt = turn.factFinishDt;
	}

	public TurnXAgr(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String finishDt,
			Double turnAmt, String monthDt, Integer customerId, Integer productId, String agreementNumber,
			String agrStartDt, String plannedFinishDt, String factFinishDt) {
		super(cardNumber, cardId, agreementId, cardStartDt, finishDt, turnAmt, monthDt);
		this.customerId = customerId;
		this.productId = productId;
		this.agreementNumber = agreementNumber;
		this.agrStartDt = agrStartDt;
		this.plannedFinishDt = plannedFinishDt;
		this.factFinishDt = factFinishDt;
	}

	public TurnXAgr(Turn turn, Integer customerId, Integer productId, String agreementNumber, String agrStartDt,
			String plannedFinishDt, String factFinishDt) {
		super(turn);
		this.customerId = customerId;
		this.productId = productId;
		this.agreementNumber = agreementNumber;
		this.agrStartDt = agrStartDt;
		this.plannedFinishDt = plannedFinishDt;
		this.factFinishDt = factFinishDt;
	}

	public TurnXAgr(Turn turn, Agreement agr) {
		super(turn);
		this.customerId = agr.customerId;
		this.productId = agr.productId;
		this.agreementNumber = agr.agreementNumber;
		this.agrStartDt = agr.startDt;
		this.plannedFinishDt = agr.plannedFinishDt;
		this.factFinishDt = agr.factFinishDt;
	}

	public TurnXAgr(String str, String cep) {
		super(str, cep);
		String arr[] = str.split(cep);
		try {
			this.customerId = Integer.valueOf(arr[7]);
			this.productId = Integer.valueOf(arr[8]);
			this.agreementNumber = arr[9];
			this.agrStartDt = arr[10];
			this.plannedFinishDt = arr[11];
			this.factFinishDt = arr[12];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public TurnXAgr(String str) {
		this(str, ";");
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public String getAgrStartDt() {
		return agrStartDt;
	}

	public void setAgrStartDt(String agrStartDt) {
		this.agrStartDt = agrStartDt;
	}

	public String getFactFinishDt() {
		return factFinishDt;
	}

	public void setFactFinishDt(String factFinishDt) {
		this.factFinishDt = factFinishDt;
	}

	@Override
	public String toString() {
		return super.toString() + ";" + customerId + ";" + productId + ";" + agreementNumber + ";" + agrStartDt + ";"
				+ plannedFinishDt + ";" + factFinishDt;
	}

}
