package com.amazonaws.services.kinesisanalytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationWithType extends Authorization {
	private static final Logger log = LoggerFactory.getLogger(AuthorizationWithType.class);
	private String authorizationTypeNm;

	public String getAuthorizationTypeNm() {
		return authorizationTypeNm;
	}

	public void setAuthorizationTypeNm(String authorizationTypeNm) {
		this.authorizationTypeNm = authorizationTypeNm;
	}
	
	public AuthorizationWithType() {
		
	}
	
	public AuthorizationWithType(String str) {
		this(str, ",");
	}
	
	public AuthorizationWithType(String str, String cep) {
		super(str, cep);
		String arr[] = str.split(cep);
		try {
			this.authorizationTypeNm = arr[5];
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}
	
	public AuthorizationWithType(int authorizationId, int authorizationTypeId, double authorizationAmt, int cardId,
			String authorizationDttm, String authorizationTypeNm) {
		super(authorizationId, authorizationTypeId, authorizationAmt, cardId, authorizationDttm);
		this.authorizationTypeNm = authorizationTypeNm;
	}
	
	public AuthorizationWithType(Authorization auth, String authorizationTypeNm) {
		super(auth);
		this.authorizationTypeNm = authorizationTypeNm;
	}
	
	public AuthorizationWithType(AuthorizationWithType auth) {
		super(auth);
		this.authorizationTypeNm = auth.authorizationTypeNm;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", authorizationTypeNm: " + authorizationTypeNm;
	}
	
}
