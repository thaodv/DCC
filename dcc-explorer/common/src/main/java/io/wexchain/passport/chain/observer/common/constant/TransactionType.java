package io.wexchain.passport.chain.observer.common.constant;

/**
 * TransactionType
 *
 * @author zhengpeng
 */
public enum TransactionType {

    /**
     *
     */
    TRADE("交易"),

    /**
     *
     */
    TRANSFER("转账");

    private String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
