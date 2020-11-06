package com.amazonaws.services.kinesisanalytics;

import java.io.Serializable;

public class AuthorizationType implements Serializable  {
	private int authorizationTypeId;
	private String authorizationTypeNm;
	
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
	
	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}
	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}

	public int getAuthorizationTypeId() {
		return authorizationTypeId;
	}

	public void setAuthorizationTypeId(int authorizationTypeId) {
		this.authorizationTypeId = authorizationTypeId;
	}

	
}
