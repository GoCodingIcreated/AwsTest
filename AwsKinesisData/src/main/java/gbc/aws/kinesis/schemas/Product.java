package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "PRODUCT")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Integer productId;
	protected String productNm;

	public Product() {
		super();
	}

	public Product(Product prod) {
		super();
		this.productId = prod.productId;
		this.productNm = prod.productNm;
	}

	@DynamoDBHashKey(attributeName = "PRODUCT_ID")
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Product(Integer productId, String productNm) {
		super();
		this.productId = productId;
		this.productNm = productNm;
	}

	@DynamoDBAttribute(attributeName = "PRODUCT_NM")
	public String getProductNm() {
		return productNm;
	}

	public void setProductNm(String productNm) {
		this.productNm = productNm;
	}

	@Override
	public String toString() {
		return "productId: " + productId + ", productNm: " + productNm + "\n";
	}

}
