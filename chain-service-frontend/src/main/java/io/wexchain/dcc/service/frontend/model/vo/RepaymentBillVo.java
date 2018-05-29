package io.wexchain.dcc.service.frontend.model.vo;

import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;

import java.math.BigDecimal;

/**
 * RepaymentBill
 *
 * @author zhengpeng
 */
public class RepaymentBillVo {

    /**
     * 链上订单ID
     */
    private Long chainOrderId;

    /**
     * 待还利息
     */
    private BigDecimal repaymentInterest;

    /**
     * 待还本金
     */
    private BigDecimal repaymentPrincipal;

    /**
     * 滞纳金
     */
    private BigDecimal overdueFine;

    /**
     * 提前还款罚金
     */
    private BigDecimal penaltyAmount;

    /**
     * 应还总金额
     */
    private BigDecimal amount;

    /**
     * 资产代码
     */
    private String assetCode;

    /**
     * 还款地址
     */
    private String repaymentAddress;

    private BigDecimal noPayAmount;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public Long getChainOrderId() {
        return chainOrderId;
    }

    public void setChainOrderId(Long chainOrderId) {
        this.chainOrderId = chainOrderId;
    }

    public BigDecimal getRepaymentInterest() {
        return repaymentInterest;
    }

    public void setRepaymentInterest(BigDecimal repaymentInterest) {
        this.repaymentInterest = repaymentInterest;
    }

    public BigDecimal getRepaymentPrincipal() {
        return repaymentPrincipal;
    }

    public void setRepaymentPrincipal(BigDecimal repaymentPrincipal) {
        this.repaymentPrincipal = repaymentPrincipal;
    }

    public BigDecimal getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(BigDecimal overdueFine) {
        this.overdueFine = overdueFine;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRepaymentAddress() {
        return repaymentAddress;
    }

    public BigDecimal getNoPayAmount() {
        return noPayAmount;
    }

    public void setNoPayAmount(BigDecimal noPayAmount) {
        this.noPayAmount = noPayAmount;
    }

    public void setRepaymentAddress(String repaymentAddress) {
        this.repaymentAddress = repaymentAddress;
    }

    public RepaymentBillVo(RepaymentBill repaymentBill) {
        this.chainOrderId = repaymentBill.getChainOrderId();
        this.repaymentInterest = repaymentBill.getRepaymentInterest();
        this.repaymentPrincipal = repaymentBill.getRepaymentPrincipal();
        this.overdueFine = repaymentBill.getOverdueFine();
        this.penaltyAmount = repaymentBill.getPenaltyAmount();
        this.amount = repaymentBill.getAmount();
        this.assetCode = repaymentBill.getAssetCode();
        this.repaymentAddress = repaymentBill.getRepaymentAddress();
        this.noPayAmount = repaymentBill.getNoPayAmount();
    }
}
