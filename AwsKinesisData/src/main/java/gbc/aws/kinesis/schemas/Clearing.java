package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clearing implements Serializable {
//	private static final Logger log = LoggerFactory.getLogger(Clearing.class);
//	private static final long serialVersionUID = 1L;
	protected Integer clearingId;
	protected Integer clearingTypeId;
	protected Integer authorizationId;
	protected Double clearingAmt;
	protected Integer cardId;
	protected String clearingDttm;
	protected String awsDttm;
	protected String processedDttm;

	public Clearing() {
		super();
	}

	public Clearing(String str) {
		this(str, ";");
	}

	public Clearing(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.clearingId = Integer.valueOf(arr[0]);
			this.clearingTypeId = Integer.valueOf(arr[1]);
			this.authorizationId = Integer.valueOf(arr[2]);
			this.clearingAmt = Double.valueOf(arr[3]);
			this.cardId = Integer.valueOf(arr[4]);
			this.clearingDttm = arr[5];
			this.awsDttm = arr[6];
			this.processedDttm = arr[7];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
//			log.warn("Not all fields was initialized: " + str);
		}
	}
	
	public Clearing(String str, String cep, boolean isFirstStepFlg) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.clearingId = Integer.valueOf(arr[0]);
			this.clearingTypeId = Integer.valueOf(arr[1]);
			this.authorizationId = Integer.valueOf(arr[2]);
			this.clearingAmt = Double.valueOf(arr[3]);
			this.cardId = Integer.valueOf(arr[4]);
			this.clearingDttm = arr[5];
			this.awsDttm = AwsKinesisData.currentTimestamp();
			this.processedDttm = AwsKinesisData.currentTimestamp();
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
//			log.warn("Not all fields was initialized: " + str);
		}
	}

	public Clearing(Clearing clr) {
		this.clearingId = clr.clearingId;
		this.clearingTypeId = clr.clearingTypeId;
		this.authorizationId = clr.authorizationId;
		this.clearingAmt = clr.clearingAmt;
		this.cardId = clr.cardId;
		this.clearingDttm = clr.clearingDttm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = clr.processedDttm;
	}
	
	public Clearing(Integer clearingId, Integer clearingTypeId, Integer authorizationId, Double clearingAmt, Integer cardId,
			String clearingDttm, String awsDttm) {
		super();
		this.clearingId = clearingId;
		this.clearingTypeId = clearingTypeId;
		this.authorizationId = authorizationId;
		this.clearingAmt = clearingAmt;
		this.cardId = cardId;
		this.clearingDttm = clearingDttm;
		this.awsDttm = awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
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

	@Override
	public String toString() {
		return clearingId + ";" + clearingTypeId + ";" + authorizationId + ";" + clearingAmt + ";" + cardId + ";"
				+ clearingDttm + ";" + awsDttm + ";" + processedDttm + "\n";
	}

	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

	public String getAwsDttm() {
		return awsDttm;
	}

	public void setAwsDttm(String awsDttm) {
		this.awsDttm = awsDttm;
	}

}
