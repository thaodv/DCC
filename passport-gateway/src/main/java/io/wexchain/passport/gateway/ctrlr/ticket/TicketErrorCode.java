package io.wexchain.passport.gateway.ctrlr.ticket;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum TicketErrorCode implements ErrorCode {

	/**
	 * 
	 */
	TICKET_INVALID("ticket invalid"),
	
	/**
	 * 
	 */
	CHALLENGE_FAILURE("challenge failure", "input:%s, expected:%s");

	private String description;

	private String template;

	private TicketErrorCode(String description) {
		this.description = description;
	}

	private TicketErrorCode(String description, String template) {
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
