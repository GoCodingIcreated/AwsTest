package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "CLEARING_TYPE")
public class ClearingType implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer clearingTypeId;
	protected String clearingTypeNm;

	public ClearingType() {
		super();
	}

	public ClearingType(Integer clearingTypeId, String clearingTypeNm) {
		super();
		this.clearingTypeId = clearingTypeId;
		this.clearingTypeNm = clearingTypeNm;
	}

	public ClearingType(ClearingType clrType) {
		super();
		this.clearingTypeId = clrType.clearingTypeId;
		this.clearingTypeNm = clrType.clearingTypeNm;
	}

	@DynamoDBAttribute(attributeName = "CLEARING_TYPE_NM")
	public String getClearingTypeNm() {
		return clearingTypeNm;
	}

	public void setClearingTypeNm(String clearingTypeNm) {
		this.clearingTypeNm = clearingTypeNm;
	}

	@DynamoDBHashKey(attributeName = "CLEARING_TYPE_ID")
	public Integer getClearingTypeId() {
		return clearingTypeId;
	}

	public void setClearingTypeId(Integer clearingTypeId) {
		this.clearingTypeId = clearingTypeId;
	}

	@Override
	public String toString() {
		return "clearingTypeId: " + clearingTypeId + ", clearingTypeNm: " + clearingTypeNm;
	}

}
