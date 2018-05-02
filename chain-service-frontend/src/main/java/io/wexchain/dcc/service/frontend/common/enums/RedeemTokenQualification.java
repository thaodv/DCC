package io.wexchain.dcc.service.frontend.common.enums;

/**
 * RedeemTokenQualification
 *
 * @author zhengpeng
 */
public enum RedeemTokenQualification {

    REDEEMED("已领取"),

    RESTRICTED("受限，不可领取"),

    AVAILABLE("可以领取");

    private String description;

    RedeemTokenQualification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
