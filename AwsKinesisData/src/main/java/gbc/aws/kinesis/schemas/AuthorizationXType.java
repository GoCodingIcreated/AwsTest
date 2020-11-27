package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AUTH_X_TYPE")
public class AuthorizationXType extends Authorization implements Serializable {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(AuthorizationXType.class);
	protected String authorizationTypeNm;

	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}

	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public AuthorizationXType() {

	}

	public AuthorizationXType(String str) {
		this(str, ";");
	}

	public AuthorizationXType(String str, String cep) {
		super(str, cep);
		String arr[] = str.split(cep);
		try {
			this.authorizationTypeNm = arr[5];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public AuthorizationXType(Integer authorizationId, Integer authorizationTypeId, Double authorizationAmt,
			Integer cardId, String authorizationDttm, String authorizationTypeNm) {
		super(authorizationId, authorizationTypeId, authorizationAmt, cardId, authorizationDttm);
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public AuthorizationXType(Authorization auth, String authorizationTypeNm) {
		super(auth);
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public AuthorizationXType(AuthorizationXType auth) {
		super(auth);
		this.authorizationTypeNm = auth.authorizationTypeNm;
	}

	public AuthorizationXType(AuthorizationXType auth, AuthorizationType authType) {
		super(auth);
		this.authorizationTypeNm = authType.authorizationTypeNm;
	}
	
	@Override
	public String toString() {
		return super.toString() + ";" + authorizationTypeNm;
	}

	@Override
	@DynamoDBHashKey(attributeName = "AUTHORIZATION_ID")
	public Integer getAuthorizationId() {
		return authorizationId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_TYPE_ID")
	public Integer getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_AMT")
	public Double getAuthorizationAmt() {
		return authorizationAmt;
	}

	@Override
	@DynamoDBAttribute(attributeName = "CARD_ID")
	public Integer getCardId() {
		return cardId;
	}

	@Override
	@DynamoDBAttribute(attributeName = "AUTHORIZATION_DTTM")
	public String getAuthorizationDttm() {
		return authorizationDttm;
	}

}
