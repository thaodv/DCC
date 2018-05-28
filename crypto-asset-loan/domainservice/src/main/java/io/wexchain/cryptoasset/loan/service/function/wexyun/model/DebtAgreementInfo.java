package io.wexchain.cryptoasset.loan.service.function.wexyun.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * DebtAgreementInfo
 *
 * @author zhengpeng
 */
public class DebtAgreementInfo {

    private String agreementId;

    /**
     * 签约时间
     */
    private DateInfo signDate;

    /**
     * 甲方
     */
    private PersonInfo creditor;

    /**
     * 乙方
     */
    private PersonInfo debtor;

    /**
     * 借币币种
     */
    private String assetCode;

    /**
     * 借币数量
     */
    private BigDecimal amount;

    /**
     * 年华收益百分比
     */
    private BigDecimal annualizedRatePercent;

    /**
     * 还款数量
     */
    private BigDecimal repaymentAmount;

    /**
     * 还币日期
     */
    private DateInfo repaymentDate;

    /**
     * 还币方式
     */
    private String repaymentType;

    private DateInfo borrowDateFrom;

    private DateInfo borrowDateTo;

    private BigDecimal fee;

    private BigDecimal overdueRatePercent;

    private BigDecimal earlyRepaymentRatePercent;


    public static class DateInfo {
        private int year;
        private int month;
        private int day;

        public DateInfo() {
        }

        public DateInfo(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public static DateInfo of(DateTime dateTime) {
            return new DateInfo(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }

    public static class PersonInfo {
        private String name;
        private String idNo;
        private String address;

        public PersonInfo(String name, String idNo, String address) {
            this.name = name;
            this.idNo = idNo;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdNo() {
            return idNo;
        }

        public void setIdNo(String idNo) {
            this.idNo = idNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public DateInfo getSignDate() {
        return signDate;
    }

    public void setSignDate(DateInfo signDate) {
        this.signDate = signDate;
    }

    public PersonInfo getCreditor() {
        return creditor;
    }

    public void setCreditor(PersonInfo creditor) {
        this.creditor = creditor;
    }

    public PersonInfo getDebtor() {
        return debtor;
    }

    public void setDebtor(PersonInfo debtor) {
        this.debtor = debtor;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAnnualizedRatePercent() {
        return annualizedRatePercent;
    }

    public void setAnnualizedRatePercent(BigDecimal annualizedRatePercent) {
        this.annualizedRatePercent = annualizedRatePercent;
    }

    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public DateInfo getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(DateInfo repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public String getRepaymentType() {
        return repaymentType;
    }

    public void setRepaymentType(String repaymentType) {
        this.repaymentType = repaymentType;
    }

    public DateInfo getBorrowDateFrom() {
        return borrowDateFrom;
    }

    public void setBorrowDateFrom(DateInfo borrowDateFrom) {
        this.borrowDateFrom = borrowDateFrom;
    }

    public DateInfo getBorrowDateTo() {
        return borrowDateTo;
    }

    public void setBorrowDateTo(DateInfo borrowDateTo) {
        this.borrowDateTo = borrowDateTo;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getOverdueRatePercent() {
        return overdueRatePercent;
    }

    public void setOverdueRatePercent(BigDecimal overdueRatePercent) {
        this.overdueRatePercent = overdueRatePercent;
    }

    public BigDecimal getEarlyRepaymentRatePercent() {
        return earlyRepaymentRatePercent;
    }

    public void setEarlyRepaymentRatePercent(BigDecimal earlyRepaymentRatePercent) {
        this.earlyRepaymentRatePercent = earlyRepaymentRatePercent;
    }
}
