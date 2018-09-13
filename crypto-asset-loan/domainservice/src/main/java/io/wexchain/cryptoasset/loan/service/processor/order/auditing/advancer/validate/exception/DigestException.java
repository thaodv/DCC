package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception;

public class DigestException extends Exception {
	private String field;

	public DigestException(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

}