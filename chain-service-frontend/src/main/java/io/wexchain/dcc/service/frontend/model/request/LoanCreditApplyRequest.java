package io.wexchain.dcc.service.frontend.model.request;

import io.wexchain.cryptoasset.loan.api.constant.DurationUnit;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * <p>
 * 信贷2.0进件请求参数
 * </p>
 *
 */
public class LoanCreditApplyRequest {

    @NotNull(message = "orderId不能为空")
    private Long orderId;

    @NotNull(message = "产品id不能为空'")
    private Long loanProductId;

    /**
     * 借款人姓名
     */
  //  @NotNull(message = "借款人姓名不能为空")
    @Size(max = 50, message = "借款人姓名长度校验失败，要求1-50位")
    private String          borrowName;

    /**
     * 借款金额
     */
    @NotNull(message = "借款金额不能为空")
    private BigDecimal borrowAmount;

    /**
     * 借款期限
     */
    @NotNull(message = "借款期限不能为空")
    private Integer         borrowDuration;
    /**
     * 借款期限单位
     */
    @NotNull(message = "借款期限单位不能为空")
    private DurationUnit durationUnit;

    /**
     * 证件号
     */
    @Size(max = 32, message = "证件号长度校验失败，最多32位")
    private String          certNo;
    /**
     * 借款人手机号
     */

    @Length(min = 11, max = 11, message = "借款人手机号长度校验失败，要求11位")
    private String          mobile;

    /**
     * 借款人预留银行卡号
     */
    @Size(max = 32, message = "银行卡号长度校验失败，最多32位")
    private String          bankCard;

    /**
     * 借款人预留银行卡绑定手机号
     */
    @Size(max = 11, message = "银行卡绑定手机号长度校验失败，最多11位")
    private String          bankMobile;

    /**
     * 申请日期
     */
    @NotNull(message = "申请日期不能为空")
    private Long applyDate;

    public Long getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Long applyDate) {
        this.applyDate = applyDate;
    }

    public BigDecimal getBorrowAmount() {
        return borrowAmount;
    }

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public void setBorrowAmount(BigDecimal borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public Integer getBorrowDuration() {
        return borrowDuration;
    }

    public void setBorrowDuration(Integer borrowDuration) {
        this.borrowDuration = borrowDuration;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankMobile() {
        return bankMobile;
    }

    public void setBankMobile(String bankMobile) {
        this.bankMobile = bankMobile;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
