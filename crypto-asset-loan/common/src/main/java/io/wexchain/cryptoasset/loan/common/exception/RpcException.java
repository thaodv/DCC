package io.wexchain.cryptoasset.loan.common.exception;

public class RpcException extends RuntimeException {

	public RpcException(Throwable cause) {
		super(cause);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(String message) {
		super(message);
	}

}
