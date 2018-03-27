package io.wexchain.passport.chain.observer.common.constant;

/**
 * 地址类型
 */
public enum AddressType {

    /**
     *
     */
    CONTRACT("合约地址"),

    /**
     *
     */
    EXTERNALLY_OWNED("外部拥有");

    private String description;

    AddressType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
