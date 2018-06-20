package io.wexchain.dcc.marketing.common.constant;

public enum MiningRewardRoundItemStatus {

    CREATED("已创建"),

    SNAPSHOTED("已打快照"),

    DELIVERED("已发放");

    private String description;

    MiningRewardRoundItemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }


}
