package io.wexchain.dcc.service.frontend.model.vo;

import io.wexchain.dcc.marketing.api.constant.RestrictionType;
import org.joda.time.DateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * 活动场景
 *
 * @author zhengpeng
 */
public class ScenarioVo {

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
    private BigInteger amount;

    /**
     * 创建时间
     */
    private DateTime createdTime;

    /**
     * 领取代币列表
     */
    private List<RedeemTokenVo> redeemTokenList;


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

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public DateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(DateTime createdTime) {
        this.createdTime = createdTime;
    }

    public List<RedeemTokenVo> getRedeemTokenList() {
        return redeemTokenList;
    }

    public void setRedeemTokenList(List<RedeemTokenVo> redeemTokenList) {
        this.redeemTokenList = redeemTokenList;
    }
}
