package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AGREEMENT")
public class Agreement implements Serializable {
	protected static final long serialVersionUID = 1L;
	protected Integer agreementId;
	protected Integer customerId;
	protected Integer productId;
	protected String agreementNumber;
	protected String startDt;
	protected String plannedFinishDt;
	protected String factFinishDt;

	public Agreement() {
		super();
	}

	public Agreement(Agreement agrmt) {
		super();
		this.agreementId = agrmt.agreementId;
		this.customerId = agrmt.customerId;
		this.productId = agrmt.productId;
		this.agreementNumber = agrmt.agreementNumber;
		this.startDt = agrmt.startDt;
		this.plannedFinishDt = agrmt.plannedFinishDt;
		this.factFinishDt = agrmt.factFinishDt;
	}

	public Agreement(Integer agreementId, Integer customerId, Integer productId, String agreementNumber, String startDt,
			String plannedFinishDt, String factFinishDt) {
		super();
		this.agreementId = agreementId;
		this.customerId = customerId;
		this.productId = productId;
		this.agreementNumber = agreementNumber;
		this.startDt = startDt;
		this.plannedFinishDt = plannedFinishDt;
		this.factFinishDt = factFinishDt;
	}

	@DynamoDBHashKey(attributeName = "AGREEMENT_ID")
	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	@DynamoDBAttribute(attributeName = "CUSTOMER_ID")
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@DynamoDBAttribute(attributeName = "PRODUCT_ID")
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@DynamoDBAttribute(attributeName = "AGREEMENT_NUMBER")
	public String getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	@DynamoDBAttribute(attributeName = "START_DT")
	public String getStartDt() {
		return startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	@DynamoDBAttribute(attributeName = "PLANNED_FINISH_DT")
	public String getPlannedFinishDt() {
		return plannedFinishDt;
	}

	public void setPlannedFinishDt(String plannedFinishDt) {
		this.plannedFinishDt = plannedFinishDt;
	}

	@DynamoDBAttribute(attributeName = "FACT_FINISH_DT")
	public String getFactFinishDt() {
		return factFinishDt;
	}

	public void setFactFinishDt(String factFinishDt) {
		this.factFinishDt = factFinishDt;
	}

	@Override
	public String toString() {
		return "agreementId:  " + agreementId + ", customerId:  " + customerId + ", productId:  " + productId
				+ ", agreementNumber:  " + agreementNumber + ", startDt:  " + startDt + ", plannedFinishDt:  "
				+ plannedFinishDt + ", factFinishDt:  " + factFinishDt;
	}

}
