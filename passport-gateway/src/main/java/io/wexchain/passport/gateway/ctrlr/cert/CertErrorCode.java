package io.wexchain.passport.gateway.ctrlr.cert;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum CertErrorCode implements ErrorCode {

	/**
	 *
	 */
	BUSINESS_NOT_FOUND("business not found"),

	;

	private String description;

	private String template;

	private CertErrorCode(String description) {
		this.description = description;
	}

	private CertErrorCode(String description, String template) {
		this.description = description;
		this.template = template;
	}

	public String getDescription() {
		return description;
	}

	public String getTemplate() {
		return template != null ? template : description;
	}

	@Override
	public String getCode() {
		return name();
	}

}
