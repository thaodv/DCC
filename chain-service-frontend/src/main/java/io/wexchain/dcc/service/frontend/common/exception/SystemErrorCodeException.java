package io.wexchain.dcc.service.frontend.common.exception;

/**
 * SystemErrorCodeException
 *
 * @author zhengpeng
 */
public class SystemErrorCodeException extends RuntimeException {
    private String errorCode;

    public SystemErrorCodeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
