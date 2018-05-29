package io.wexchain.passport.gateway.ctrlr.commons;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class QueryOrderParam {

	@Min(0L)
	private int from;

	@Min(0L)
	private int to;

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}
}
