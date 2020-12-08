package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "CLEARING_TYPE")
public class ClearingType implements Serializable {	
	private static final Logger log = LoggerFactory.getLogger(ClearingType.class);
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

	public ClearingType(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.clearingTypeId = Integer.valueOf(arr[0]);
			this.clearingTypeNm = arr[1];							
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public ClearingType(String str) {
		this(str, ";");
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
		return clearingTypeId + ";" + clearingTypeNm + "\n";
	}

}
