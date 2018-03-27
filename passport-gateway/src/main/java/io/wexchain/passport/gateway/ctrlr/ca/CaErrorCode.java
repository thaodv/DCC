package io.wexchain.passport.gateway.ctrlr.ca;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum CaErrorCode implements ErrorCode {

	/**
	 * 
	 */
	TICKET_INVALID("ticket invalid"),

	/**
	 * 
	 */
	CHALLENGE_FAILURE("challenge failure", "input:%s, expected:%s"),

	/**
	 * 
	 */
	SIGN_MESSAGE_INVALID("signMessaage invalid", "hint:%s"),

	;

	private String description;

	private String template;

	private CaErrorCode(String description) {
		this.description = description;
	}

	private CaErrorCode(String description, String template) {
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
