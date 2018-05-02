package io.wexchain.dcc.sdk.client.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChallengeForm {
	@Size(max = 100)
	@NotBlank
	private String ticket;
	
	@Size(max = 100)
	private String code;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
