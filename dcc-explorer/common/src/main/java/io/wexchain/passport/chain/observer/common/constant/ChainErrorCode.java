package io.wexchain.passport.chain.observer.common.constant;

import com.wexmarket.topia.commons.rpc.error.ErrorCode;

public enum ChainErrorCode implements ErrorCode {

    BLOCK_NOT_FOUND("区块未找到"),

    TRANSACTION_NOT_FOUND("交易未找到"),

    DATA_NOT_FOUND("数据未找到");

    private String description;

    private String template;

    ChainErrorCode(String description) {
        this.description = description;
    }

    ChainErrorCode(String description, String template) {
        this.description = description;
        this.template = template;
    }

    public String getDescription() {
        return description;
    }

    public String getTemplate() {
        return template != null ? template : description;
    }

    public String getCode() {
        return name();
    }

}
