/*
 * Copyright 2013 weibopay.com, Inc. All rights reserved.
 */
package io.wexchain.dcc.service.frontend.common.enums;

/**
 * <p>
 * 签名类型
 * </p>
 * 
 * @author yanyi
 */
public enum SignTypeKind {

                          MD5("MD5"), RSA("RSA"), DSA("DSA");

    private String code;

    private SignTypeKind(String code) {
        this.code = code;

    }

    public static SignTypeKind getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (SignTypeKind ls : SignTypeKind.values()) {
            if (ls.code.equalsIgnoreCase(code)) {
                return ls;
            }
        }
        return null;
    }

    public boolean equals(String code) {
        return getCode().equalsIgnoreCase(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
