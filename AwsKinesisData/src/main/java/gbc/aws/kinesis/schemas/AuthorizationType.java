package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AUTHORIZATION_TYPE")
public class AuthorizationType implements Serializable {	
	private static final Logger log = LoggerFactory.getLogger(AuthorizationType.class);
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
	
	public AuthorizationType(String str, String cep) {
		String arr[] = str.replace("\n", "").split(cep);
		try {
			this.authorizationTypeId = Integer.valueOf(arr[0]);
			this.authorizationTypeNm = arr[1];							
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public AuthorizationType(String str) {
		this(str, ";");
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

	@Override
	public String toString() {
		return authorizationTypeId + ";" + authorizationTypeNm + "\n";
	}

}
