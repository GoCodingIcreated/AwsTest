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

	public Bucket() {

	}

	public Bucket(Bucket bucket) {
		this.customerId = bucket.customerId;
		this.monthDt = bucket.monthDt;
		this.customerTurnAmt = bucket.customerTurnAmt;
	}

	public Bucket(TurnXAgrXProd turn) {
		this.customerId = turn.customerId;
		this.monthDt = turn.monthDt;
		this.customerTurnAmt = turn.turnAmt;
	}

	public Bucket(String str, String cep) {
		String arr[] = str.split(cep);
		try {
			this.customerId = Integer.valueOf(arr[0]);
			this.monthDt = arr[1];
			this.customerTurnAmt = Double.valueOf(arr[2]);
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
	}

	public Bucket(Integer customerId, String monthDt, Double customerTurnAmt) {
		super();
		this.customerId = customerId;
		this.monthDt = monthDt;
		this.customerTurnAmt = customerTurnAmt;
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
		return customerId + ";" + monthDt + ";" + customerTurnAmt;
	}

}
