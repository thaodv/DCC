package io.wexchain.passport.gateway.ctrlr.erc20;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum Erc20ErrorCode implements ErrorCode {

	/**
	 *
	 */
	BUSINESS_NOT_FOUND("business not found"),

	;

	private String description;

	private String template;

	private Erc20ErrorCode(String description) {
		this.description = description;
	}

	private Erc20ErrorCode(String description, String template) {
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
