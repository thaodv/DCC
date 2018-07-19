package io.wexchain.dcc.service.frontend.model.vo;


import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author zhengpeng
 */
public class EcoBonusRuleVo {

    /**
     *
     */
    private String groupCode;

    /**
     * 名称
     */
    private String bonusName;

    /**
     * 收款地址
     */
    private String bonusCode;

    /**
     * 金额
     */
    private BigDecimal bonusAmount;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getBonusName() {
        return bonusName;
    }

    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }

    public String getBonusCode() {
        return bonusCode;
    }

    public void setBonusCode(String bonusCode) {
        this.bonusCode = bonusCode;
    }

    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public EcoBonusRuleVo(String groupCode, String bonusName, String bonusCode, BigDecimal bonusAmount) {
        this.groupCode = groupCode;
        this.bonusName = bonusName;
        this.bonusCode = bonusCode;
        this.bonusAmount = bonusAmount;
    }

    public EcoBonusRuleVo() {
    }
}
