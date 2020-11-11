package com.amazonaws.services.kinesisanalytics;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;

import java.io.Serializable;

@DynamoDBTable(tableName="AUTHORIZATION_TYPE")
public class AuthorizationType implements Serializable  {
	private int authorizationTypeId;
	private String authorizationTypeNm;
	
	public AuthorizationType() {
		super();
	}
	
	public AuthorizationType(int authorizationTypeId, String authorizationTypeNm) {
		super();
		this.authorizationTypeId = authorizationTypeId;
		this.authorizationTypeNm = authorizationTypeNm;
	}
	
	public AuthorizationType(AuthorizationType authType) {
		super();
		this.authorizationTypeId = authType.authorizationTypeId;
		this.authorizationTypeNm = authType.authorizationTypeNm;
	}
	
	@DynamoDBAttribute(attributeName="AUTHORIZATION_TYPE_NM")
	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}
	
	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	@DynamoDBHashKey(attributeName="AUTHORIZATION_TYPE_ID")
	public int getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	public void setAuthorizationTypeId(int authorizationTypeId) {
		this.authorizationTypeId = authorizationTypeId;
	}

	
}
