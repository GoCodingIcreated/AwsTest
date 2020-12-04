package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bucket implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(Bucket.class);
	private static final long serialVersionUID = 1L;
	protected Integer customerId;
	protected String monthDt;
	protected Double customerTurnAmt;
	protected String authAwsDttm;
	protected String clrAwsDttm;
	private String processedDttm;

	public Bucket() {

	}

	public Bucket(Bucket bucket) {
		this.customerId = bucket.customerId;
		this.monthDt = bucket.monthDt;
		this.customerTurnAmt = bucket.customerTurnAmt;
		this.authAwsDttm = bucket.authAwsDttm;
		this.clrAwsDttm = bucket.clrAwsDttm;
		this.processedDttm = bucket.processedDttm;

	}

	public Bucket(TurnXAgrXProd turn) {
		this.customerId = turn.customerId;
		this.monthDt = turn.monthDt;
		this.customerTurnAmt = turn.turnAmt;
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public Bucket(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.customerId = Integer.valueOf(arr[0]);
			this.monthDt = arr[1];
			this.customerTurnAmt = Double.valueOf(arr[2]);
			this.authAwsDttm = arr[3];
			this.clrAwsDttm = arr[4];
			this.processedDttm = arr[5];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public Bucket(String str) {
		this(str, ";");
	}

	public Bucket(Bucket bucket, TurnXAgrXProd turn) {
		this(bucket);
		this.customerTurnAmt += turn.turnAmt;
		this.authAwsDttm = turn.authAwsDttm;
		this.clrAwsDttm = turn.clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public Bucket(Integer customerId, String monthDt, Double customerTurnAmt, String authAwsDttm, String clrAwsDttm) {
		super();
		this.customerId = customerId;
		this.monthDt = monthDt;
		this.customerTurnAmt = customerTurnAmt;
		this.authAwsDttm = authAwsDttm;
		this.clrAwsDttm = clrAwsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getMonthDt() {
		return monthDt;
	}

	public void setMonthDt(String monthDt) {
		this.monthDt = monthDt;
	}

	public Double getCustomerTurnAmt() {
		return customerTurnAmt;
	}

	public void setCustomerTurnAmt(Double customerTurnAmt) {
		this.customerTurnAmt = customerTurnAmt;
	}

	@Override
	public String toString() {
		return customerId + ";" + monthDt + ";" + customerTurnAmt + ";" + authAwsDttm + ";" + clrAwsDttm + ";" + processedDttm + "\n";
	}

	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

	public String getAuthAwsDttm() {
		return authAwsDttm;
	}

	public void setAuthAwsDttm(String authAwsDttm) {
		this.authAwsDttm = authAwsDttm;
	}

	public String getClrAwsDttm() {
		return clrAwsDttm;
	}

	public void setClrAwsDttm(String clrAwsDttm) {
		this.clrAwsDttm = clrAwsDttm;
	}

}
