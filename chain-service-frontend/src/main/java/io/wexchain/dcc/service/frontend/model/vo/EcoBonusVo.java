package io.wexchain.dcc.service.frontend.model.vo;


import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author zhengpeng
 */
public class EcoBonusVo {

    /**
     */
    private Long id;

    /**
     * 收款地址
     */
    private String receiverAddress;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String memo;

    /**
     * 创建时间
     */
    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
