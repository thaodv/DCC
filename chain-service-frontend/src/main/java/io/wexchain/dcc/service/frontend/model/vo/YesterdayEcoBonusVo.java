package io.wexchain.dcc.service.frontend.model.vo;


import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author zhengpeng
 */
public class YesterdayEcoBonusVo {

    /**
     * 昨日获得生态值
     */
    private BigDecimal yesterdayAmount;

    /**
     * 获得奖励
     */
    private BigDecimal amount;

    public BigDecimal getYesterdayAmount() {
        return yesterdayAmount;
    }

    public void setYesterdayAmount(BigDecimal yesterdayAmount) {
        this.yesterdayAmount = yesterdayAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
