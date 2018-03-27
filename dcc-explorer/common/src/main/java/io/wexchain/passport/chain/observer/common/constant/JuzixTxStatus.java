package io.wexchain.passport.chain.observer.common.constant;

/**
 * 交易状态
 */
public enum JuzixTxStatus {

    /**
     *
     */
    SUCCESS("成功"),

    /**
     *
     */
    FAIL("失败");

    private String description;

    JuzixTxStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
