package com.amazonaws.services.kinesisanalytics;

import java.io.Serializable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authorization implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(Authorization.class);
	private int authorizationId;
	private int authorizationTypeId;
	private double authorizationAmt;
	private int cardId;
	private String authorizationDttm;
	
	public int getAuthorizationId() {
		return authorizationId;
	}
	public void setAuthorizationId(int authorizationId) {
		this.authorizationId = authorizationId;
	}
	public int getAuthorizationTypeId() {
		return authorizationTypeId;
	}
	public void setAuthorizationTypeId(int authorizationTypeId) {
		this.authorizationTypeId = authorizationTypeId;
	}
	public double getAuthorizationAmt() {
		return authorizationAmt;
	}
	public void setAuthorizationAmt(double authorizationAmt) {
		this.authorizationAmt = authorizationAmt;
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public String getAuthorizationDttm() {
		return authorizationDttm;
	}
	public void setAuthorizationDttm(String authorizationDttm) {
		this.authorizationDttm = authorizationDttm;
	}
	
	public Authorization() {
		
	}
	
	public Authorization(Authorization auth) {
		this.authorizationId = auth.authorizationId;
		this.authorizationTypeId = auth.authorizationTypeId;
		this.authorizationAmt = auth.authorizationAmt;
		this.cardId = auth.cardId;
		this.authorizationDttm = auth.authorizationDttm;
	}
	
	public Authorization(String str) {
		this(str, ",");
	}
	
	public Authorization(String str, String cep) {
		String arr[] = str.split(cep);
		try {
			this.authorizationId = Integer.valueOf(arr[0]);
			this.authorizationTypeId = Integer.valueOf(arr[1]);
			this.authorizationAmt = Double.valueOf(arr[2]);
			this.cardId = Integer.valueOf(arr[3]);
			this.authorizationDttm = arr[4];
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}
	
	public Authorization(int authorizationId, int authorizationTypeId, double authorizationAmt, int cardId,
			String authorizationDttm) {
		super();
		this.authorizationId = authorizationId;
		this.authorizationTypeId = authorizationTypeId;
		this.authorizationAmt = authorizationAmt;
		this.cardId = cardId;
		this.authorizationDttm = authorizationDttm;
	}
	
	@Override
	public String toString() {
		return "authorizationId : " + authorizationId
				+ ", authorizationTypeId: " + authorizationTypeId
				+ ", authorizationAmt: " + authorizationAmt
				+ ", cardId: " + cardId
				+ ", authorizationDttm: " + authorizationDttm;
	}
	
	
}
