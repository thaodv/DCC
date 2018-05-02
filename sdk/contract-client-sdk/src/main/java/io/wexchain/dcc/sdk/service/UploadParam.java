package io.wexchain.dcc.sdk.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UploadParam {
	private String ticket;
	private String signMessage;

	public UploadParam(String ticket, String signMessage) {
		super();
		this.ticket = ticket;
		this.signMessage = signMessage;
	}

	public String getTicket() {
		return ticket;
	}

	public String getSignMessage() {
		return signMessage;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("signMessage", this.signMessage)
				.append("ticket", this.ticket).toString();
	}

}
