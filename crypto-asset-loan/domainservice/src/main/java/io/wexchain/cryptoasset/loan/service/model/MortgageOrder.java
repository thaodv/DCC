package io.wexchain.cryptoasset.loan.service.model;



import java.math.BigDecimal;
import java.util.Date;

/**
 * LoanReport
 *
 * @author zhengpeng
 */
public class MortgageOrder {

    /**
     * 放币机构
     */
    private String deliverDept;

    /**
     * 借币金额
     */
    private BigDecimal borrowerAmount;

    /**
     * 放款时间
     */
    private Date deliverTime;

    /**
     * 借款人地址
     */
    private String borrowerAddress;

    /**
     * 机构地址
     */
    private String deptAddress;

    /**
     * 账单开始时间
     */
    private Date billStartDate;

    /**
     * 到期时间
     */
    private Date duration;

    /**
     * 借币类型
     */
    private String loanType;

    /**
     * 抵押金额
     */
    private BigDecimal mortgageAmount;

    /**
     * 抵押币种
     */
    private String mortgageUnit;

    /**
     * 借币币种
     */
    private String borrowerUnit;

    /**
     * 借币时间
     */
    private Date applyTime;

    /**
     * 最后还款时间时间
     */
    private Date lastRepaymentTime;

    /**
     * 还币利息
     */
    private BigDecimal repaymentInterest;

    /**
     * 还币本金
     */
    private BigDecimal repaymentPrincipal;

    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }


    public String getDeliverDept() {
        return deliverDept;
    }

    public void setDeliverDept(String deliverDept) {
        this.deliverDept = deliverDept;
    }

    public BigDecimal getBorrowerAmount() {
        return borrowerAmount;
    }

    public void setBorrowerAmount(BigDecimal borrowerAmount) {
        this.borrowerAmount = borrowerAmount;
    }

    public Date getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Date deliverTime) {
        this.deliverTime = deliverTime;
    }

    public String getDeptAddress() {
        return deptAddress;
    }

    public void setDeptAddress(String deptAddress) {
        this.deptAddress = deptAddress;
    }

    public Date getBillStartDate() {
        return billStartDate;
    }

    public void setBillStartDate(Date billStartDate) {
        this.billStartDate = billStartDate;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getMortgageAmount() {
        return mortgageAmount;
    }

    public void setMortgageAmount(BigDecimal mortgageAmount) {
        this.mortgageAmount = mortgageAmount;
    }

    public String getMortgageUnit() {
        return mortgageUnit;
    }

    public void setMortgageUnit(String mortgageUnit) {
        this.mortgageUnit = mortgageUnit;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getLastRepaymentTime() {
        return lastRepaymentTime;
    }

    public void setLastRepaymentTime(Date lastRepaymentTime) {
        this.lastRepaymentTime = lastRepaymentTime;
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

    public String getBorrowerUnit() {
        return borrowerUnit;
    }

    public void setBorrowerUnit(String borrowerUnit) {
        this.borrowerUnit = borrowerUnit;
    }

    public MortgageOrder() {
    }
}
