package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearingXType extends Clearing  implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(ClearingXType.class);
	private static final long serialVersionUID = 1L;
	protected String clearingTypeNm;
	private String processedDttm;
	
	public ClearingXType() {
		super();
	}

	public ClearingXType(Integer clearingId, Integer clearingTypeCd, Integer authorizationId, Double clearingAmt, Integer cardId,
			String clearingDttm, String clearingTypeNm, String awsDttm) {
		super(clearingId, clearingTypeCd, authorizationId, clearingAmt, cardId, clearingDttm, awsDttm);
		this.clearingTypeNm = clearingTypeNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public ClearingXType(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.clearingTypeNm = arr[8];
			this.awsDttm = arr[9];
			this.processedDttm = arr[10];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public ClearingXType(String str) {
		this(str, ";");
	}
	
	public ClearingXType(ClearingXType clr) {
		super(clr);
		this.clearingTypeNm = clr.clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = clr.processedDttm;
	}
	

	public ClearingXType(Clearing clr, String clearingTypeNm) {
		super(clr);
		this.clearingTypeNm = clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}
	
	public ClearingXType(Clearing clr, ClearingType clrType) {
		super(clr);
		this.clearingTypeNm = clrType.clearingTypeNm;
		this.awsDttm = clr.awsDttm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public String getClearingTypeNm() {
		return clearingTypeNm;
	}

	public void setClearingTypeNm(String clearingTypeNm) {
		this.clearingTypeNm = clearingTypeNm;
	}

	@Override
	public String toString() {
		return super.toString().replace("\n", "") + ";" + clearingTypeNm + ";" + awsDttm + ";" + processedDttm + "\n";
	}

	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}


}
