package io.wexchain.dcc.service.frontend.model.vo;


import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.model.Bill;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * LoanReport
 *
 * @author zhengpeng
 */
public class LoanReportVo {

    /**
     * 产品ID
     */
    private String lenderName;

    /**
     * 链上订单ID
     */
    private Long chainOrderId;

    /**
     * 放款时间
     */
    private Date deliverDate;

    /**
     * 还款账单
     */
    private List<Bill> billList;

    /**
     * 借币期限
     */
    private Date borrowDurationFrom;

    /**
     * 借币期限
     */
    private Date borrowDurationTo;

    /**
     * 借币时间
     */
    private Date applyDate;

    /**
     * 借币金额
     */
    private BigDecimal amount;


    /**
     * 借币金额
     */
    private String assetCode;

    private String loanWay = "信用";

    /**
     * 状态
     */
    private LoanOrderStatus status;

    public String getLoanWay() {
        return loanWay;
    }

    public void setLoanWay(String loanWay) {
        this.loanWay = loanWay;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public Long getChainOrderId() {
        return chainOrderId;
    }

    public void setChainOrderId(Long chainOrderId) {
        this.chainOrderId = chainOrderId;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public List<Bill> getBillList() {
        return billList;
    }

    public void setBillList(List<Bill> billList) {
        this.billList = billList;
    }

    public Date getBorrowDurationFrom() {
        return borrowDurationFrom;
    }

    public void setBorrowDurationFrom(Date borrowDurationFrom) {
        this.borrowDurationFrom = borrowDurationFrom;
    }

    public Date getBorrowDurationTo() {
        return borrowDurationTo;
    }

    public void setBorrowDurationTo(Date borrowDurationTo) {
        this.borrowDurationTo = borrowDurationTo;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public LoanOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LoanOrderStatus status) {
        this.status = status;
    }
}
