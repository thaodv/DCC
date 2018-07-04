package com.xxy.maple.tllibrary.exception;

/**
 * <p>自定义异常</p>
 *
 * @author Yang
 * @date 2017/12/24
 */
public class ApiException extends RuntimeException {

    public ApiException() {

    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }
}

