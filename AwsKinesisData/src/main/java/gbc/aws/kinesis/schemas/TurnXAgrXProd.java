package gbc.aws.kinesis.schemas;

import java.io.Serializable;

public class TurnXAgrXProd extends TurnXAgr implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String productNm;

	public TurnXAgrXProd() {

	}

	public TurnXAgrXProd(TurnXAgr turn, Product prod) {
		super(turn);
		this.productNm = prod.productNm;
	}

	public TurnXAgrXProd(TurnXAgr turn, String productNm) {
		super(turn);
		this.productNm = productNm;
	}

	public TurnXAgrXProd(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String finishDt,
			Double turnAmt, String monthDt, Integer customerId, Integer productId, String agreementNumber,
			String agrStartDt, String plannedFinishDt, String factFinishDt, String productNm) {
		super(cardNumber, cardId, agreementId, cardStartDt, finishDt, turnAmt, monthDt, customerId, productId,
				agreementNumber, agrStartDt, plannedFinishDt, factFinishDt);
		this.productNm = productNm;
	}

	public String getProductNm() {
		return productNm;
	}

	public void setProductNm(String productNm) {
		this.productNm = productNm;
	}

	@Override
	public String toString() {
		return super.toString() + ", productNm: " + productNm;
	}

}
