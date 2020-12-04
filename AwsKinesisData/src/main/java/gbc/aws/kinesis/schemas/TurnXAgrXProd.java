package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurnXAgrXProd extends TurnXAgr implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(TurnXAgrXProd.class);
	private static final long serialVersionUID = 1L;
	protected String productNm;
	private String processedDttm;
	
	public TurnXAgrXProd() {

	}

	public TurnXAgrXProd(TurnXAgr turn, Product prod) {
		super(turn);
		this.productNm = prod.productNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TurnXAgrXProd(TurnXAgr turn, String productNm) {
		super(turn);
		this.productNm = productNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TurnXAgrXProd(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String finishDt,
			Double turnAmt, String monthDt, Integer customerId, Integer productId, String agreementNumber,
			String agrStartDt, String plannedFinishDt, String factFinishDt, String productNm, String authAwsDttm, String clrAwsDttm) {
		super(cardNumber, cardId, agreementId, cardStartDt, finishDt, turnAmt, monthDt, customerId, productId,
				agreementNumber, agrStartDt, plannedFinishDt, factFinishDt, authAwsDttm, clrAwsDttm);
		this.productNm = productNm;
		this.processedDttm = AwsKinesisData.currentTimestamp();
	}

	public TurnXAgrXProd(String str) {
		this(str, ";");
	}

	public TurnXAgrXProd(String str, String cep) {
		super(str, cep);
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.productNm = arr[17];
			this.processedDttm = arr[18];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public String getProductNm() {
		return productNm;
	}

	public void setProductNm(String productNm) {
		this.productNm = productNm;
	}

	@Override
	public String toString() {
		return super.toString().replace("\n", "") + ";" + productNm + ";" + processedDttm + "\n";
	}

	public String getProcessedDttm() {
		return processedDttm;
	}

	public void setProcessedDttm(String processedDttm) {
		this.processedDttm = processedDttm;
	}

}
