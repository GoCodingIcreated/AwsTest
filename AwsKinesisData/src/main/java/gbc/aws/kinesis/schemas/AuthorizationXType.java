package gbc.aws.kinesis.schemas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationXType extends Authorization {
	protected static final long serialVersionUID = 1L;
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

	public AuthorizationXType(Integer authorizationId, Integer authorizationTypeId, Double authorizationAmt, Integer cardId,
			String authorizationDttm, String authorizationTypeNm) {
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

	@Override
	public String toString() {
		return super.toString() + ", authorizationTypeNm: " + authorizationTypeNm;
	}

}
