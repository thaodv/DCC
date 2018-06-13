package io.wexchain.dcc.service.frontend.model.vo;

import io.wexchain.dcc.service.frontend.common.enums.RedeemTokenQualification;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * RedeemTokenQualificationVo
 *
 * @author zhengpeng
 */
public class RedeemTokenQualificationVo {

    /**
     * 活动code
     */
    private String activityCode;

    /**
     * 场景code
     */
    private String code;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 奖励数量
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private DateTime createdTime;

    /**
     * 领取资格
     */
    private RedeemTokenQualification qualification;

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public DateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(DateTime createdTime) {
        this.createdTime = createdTime;
    }

    public RedeemTokenQualification getQualification() {
        return qualification;
    }

    public void setQualification(RedeemTokenQualification qualification) {
        this.qualification = qualification;
    }
}
