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
	private String processedDttm;

	public TurnXAgr() {

	}


	public TurnXAgr(TurnXAgr turn) {
		super(turn);
		this.customerId = turn.customerId;
		this.productId = turn.productId;
		this.agreementNumber = turn.agreementNumber;
		this.agrStartDt = turn.agrStartDt;
		this.plannedFinishDt = turn.plannedFinishDt;
		this.factFinishDt = turn.factFinishDt;
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = turn.processedDttm;
	}

	public TurnXAgr(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String finishDt,
			Double turnAmt, String monthDt, Integer customerId, Integer productId, String agreementNumber,
			String agrStartDt, String plannedFinishDt, String factFinishDt, String authAwsDttm, String clrAwsDttm) {
		super(cardNumber, cardId, agreementId, cardStartDt, finishDt, turnAmt, monthDt, authAwsDttm, clrAwsDttm);
		this.customerId = customerId;
		this.productId = productId;
		this.agreementNumber = agreementNumber;
		this.agrStartDt = agrStartDt;
		this.plannedFinishDt = plannedFinishDt;
		this.factFinishDt = factFinishDt;
		this.authAwsDttm = authAwsDttm;
		this.clrAwsDttm = clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
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
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TurnXAgr(Turn turn, Agreement agr) {
		super(turn);
		this.customerId = agr.customerId;
		this.productId = agr.productId;
		this.agreementNumber = agr.agreementNumber;
		this.agrStartDt = agr.startDt;
		this.plannedFinishDt = agr.plannedFinishDt;
		this.factFinishDt = agr.factFinishDt;
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TurnXAgr(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			if (!arr[10].equalsIgnoreCase("null")) {
				this.customerId = Integer.valueOf(arr[10]);
			}
			if (!arr[11].equalsIgnoreCase("null")) {
				this.productId = Integer.valueOf(arr[11]);
			}
			this.agreementNumber = arr[12];
			this.agrStartDt = arr[13];
			this.plannedFinishDt = arr[14];
			this.factFinishDt = arr[15];
			this.processedDttm = arr[16];
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
		return super.toString().replace("\n", "") + ";" + customerId + ";" + productId + ";" + agreementNumber + ";" + agrStartDt + ";"
				+ plannedFinishDt + ";" + factFinishDt + ";" + processedDttm + "\n";
	}


	public String getPlannedFinishDt() {
		return plannedFinishDt;
	}


	public void setPlannedFinishDt(String plannedFinishDt) {
		this.plannedFinishDt = plannedFinishDt;
	}


	public String getProcessedDttm() {
		return processedDttm;
	}


	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

}
