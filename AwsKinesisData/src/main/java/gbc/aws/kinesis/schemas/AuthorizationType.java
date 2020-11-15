package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AUTHORIZATION_TYPE")
public class AuthorizationType implements Serializable {
	protected static final long serialVersionUID = 1L;
	protected Integer authorizationTypeId;
	protected String authorizationTypeNm;

	public AuthorizationType() {
		super();
	}

	public AuthorizationType(Integer authorizationTypeId, String authorizationTypeNm) {
		super();
		this.authorizationTypeId = authorizationTypeId;
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public AuthorizationType(AuthorizationType authType) {
		super();
		this.authorizationTypeId = authType.authorizationTypeId;
		this.authorizationTypeNm = authType.authorizationTypeNm;
	}

	@DynamoDBAttribute(attributeName = "AUTHORIZATION_TYPE_NM")
	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}

	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	@DynamoDBHashKey(attributeName = "AUTHORIZATION_TYPE_ID")
	public Integer getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	public void setAuthorizationTypeId(Integer authorizationTypeId) {
		this.authorizationTypeId = authorizationTypeId;
	}

}
