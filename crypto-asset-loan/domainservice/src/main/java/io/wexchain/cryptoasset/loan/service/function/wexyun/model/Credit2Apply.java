package io.wexchain.cryptoasset.loan.service.function.wexyun.model;

import com.wexyun.open.api.domain.common.Money;
import com.wexyun.open.api.domain.credit2.ContactInfo;
import com.wexyun.open.api.domain.credit2.LoanInfo;
import com.wexyun.open.api.domain.credit2.PhotocopyFood;
import com.wexyun.open.api.domain.regular.agreement.DebtAgreement;
import com.wexyun.open.api.enums.CertType;
import com.wexyun.open.api.enums.DurationType;
import com.weihui.finance.common.credit2.enums.ApplySourceType;
import com.wexyun.open.api.enums.credit2.CreditApplyStatus;
import com.wexyun.open.api.enums.credit2.CreditProcessStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 信贷2.0进件信息
 * </p>
 *
 * @author huangfeitao
 * @version $Id: Credit2Apply.java, v 0.1 2017-05-25 上午20:52:37 huangfeitao Exp $
 */
public class Credit2Apply {

    /**
     * 申请ID
     */
    private String              applyId;
    /**
     * 进件类型
     */
    private ApplySourceType     applySourceType;
    /**
     * 借款人ID
     */
    private String              borrowerId;
    /**
     * 债权人ID
     */
    private String              loanerId;
    /**
     * 申请会员ID
     */
    private String              memberId;
    /**
     * 合作伙伴ID
     */
    private String              partnerId;
    /**
     * 商家ID或代理商ID
     */
    private String              agentId;
    /**
     * 借款人姓名
     */
    private String              borrowName;
    /**
     * 贷款类型
     */
    private String              loanType;
    /**
     * 借款用途
     */
    private String              borrowUse;
    /**
     * 借款金额
     */
    private Money               borrowAmount;
    /**
     * 借款期限
     */
    private Integer             borrowDuration;
    /**
     * 借款期限单位
     */
    private DurationType        durationType;
    /**
     * 证件号
     */
    private String              certNo;
    /**
     * 证件类型
     */
    private CertType            certType;
    /**
     * 借款人手机号
     */
    private String              mobile;
    /**
     * 申请状态
     */
    private CreditApplyStatus   status;
    /**
     * 内容模板ID
     */
    private Long                templateId;
    /**
     * 借款申请协议控制ID
     */
    private Long                ctlId;

    /**
     * 申请日期
     */
    private Date                applyDate;
    /**
     * 备注
     */
    private String              memo;
    /**
     * 扩展信息
     */
    private Map<String, String> extension;
    /**
     * 创建时间
     */
    private Date                gmtCreate;

    /**
     * 修改时间
     */
    private Date                gmtModified;

    /**
     * 条目信息
     */
    private Map<String, String> itemDetailMap;
    /**
     * 协议标题(仅签约阶段有值)
     */
    private String              agreementTitle;
    /**
     * debt协议id
     */
    private String              agreementId;
    /**
     * 外部申请ID
     */
    private String              externalApplyId;
    /**
     * 收款方ID
     */
    private String              receiveMemberId;
    /**
     * 还款方ID
     */
    private String              repaymentMemberId;

    /**
     * 贷款资料列表
     */
    private List<PhotocopyFood> photocopyFoodList;
    /**
     * 贷款协议信息
     */
    private LoanInfo            loanInfo;

    /**
     * 借款人预留银行卡号
     */
    private String              bankCard;

    /**
     * 借款人预留银行卡绑定手机号
     */
    private String              bankMobile;

    /**
     * 贷款申请处理状态
     */
    private CreditProcessStatus processStatus;

    /**
     * 进件数据来源方标识
     */
    private String              sourceIdentity;

    /**
     * 联系人信息
     */
    List<ContactInfo>           contactInfoList;

    /**
     * 关联的借款协议信息：仅当进件通过准入后才会创建协议，故此字段可能为null
     */
    private DebtAgreement       debtAgreement;

    /**
     * 打款时间
     */
    private Date                remitTime;

    /**
     * 贷款分类 LoanCategory#code
     */
    private String              loanCategory;

    /**
     * 贷款产品名称
     */
    private String              loanName;

    public Date getRemitTime() {
        return remitTime;
    }

    public void setRemitTime(Date remitTime) {
        this.remitTime = remitTime;
    }

    public String getLoanCategory() {
        return loanCategory;
    }

    public void setLoanCategory(String loanCategory) {
        this.loanCategory = loanCategory;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public DebtAgreement getDebtAgreement() {
        return debtAgreement;
    }

    public void setDebtAgreement(DebtAgreement debtAgreement) {
        this.debtAgreement = debtAgreement;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public ApplySourceType getApplySourceType() {
        return applySourceType;
    }

    public void setApplySourceType(ApplySourceType applySourceType) {
        this.applySourceType = applySourceType;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getLoanerId() {
        return loanerId;
    }

    public void setLoanerId(String loanerId) {
        this.loanerId = loanerId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getBorrowUse() {
        return borrowUse;
    }

    public void setBorrowUse(String borrowUse) {
        this.borrowUse = borrowUse;
    }

    public Money getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(Money borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public Integer getBorrowDuration() {
        return borrowDuration;
    }

    public void setBorrowDuration(Integer borrowDuration) {
        this.borrowDuration = borrowDuration;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public CertType getCertType() {
        return certType;
    }

    public void setCertType(CertType certType) {
        this.certType = certType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public CreditApplyStatus getStatus() {
        return status;
    }

    public void setStatus(CreditApplyStatus status) {
        this.status = status;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getCtlId() {
        return ctlId;
    }

    public void setCtlId(Long ctlId) {
        this.ctlId = ctlId;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Map<String, String> getItemDetailMap() {
        return itemDetailMap;
    }

    public void setItemDetailMap(Map<String, String> itemDetailMap) {
        this.itemDetailMap = itemDetailMap;
    }

    public String getAgreementTitle() {
        return agreementTitle;
    }

    public void setAgreementTitle(String agreementTitle) {
        this.agreementTitle = agreementTitle;
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public String getExternalApplyId() {
        return externalApplyId;
    }

    public void setExternalApplyId(String externalApplyId) {
        this.externalApplyId = externalApplyId;
    }

    public String getReceiveMemberId() {
        return receiveMemberId;
    }

    public void setReceiveMemberId(String receiveMemberId) {
        this.receiveMemberId = receiveMemberId;
    }

    public String getRepaymentMemberId() {
        return repaymentMemberId;
    }

    public void setRepaymentMemberId(String repaymentMemberId) {
        this.repaymentMemberId = repaymentMemberId;
    }

    public List<PhotocopyFood> getPhotocopyFoodList() {
        return photocopyFoodList;
    }

    public void setPhotocopyFoodList(List<PhotocopyFood> photocopyFoodList) {
        this.photocopyFoodList = photocopyFoodList;
    }

    public LoanInfo getLoanInfo() {
        return loanInfo;
    }

    public void setLoanInfo(LoanInfo loanInfo) {
        this.loanInfo = loanInfo;
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

    public CreditProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(CreditProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public String getSourceIdentity() {
        return sourceIdentity;
    }

    public void setSourceIdentity(String sourceIdentity) {
        this.sourceIdentity = sourceIdentity;
    }

    public List<ContactInfo> getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList(List<ContactInfo> contactInfoList) {
        this.contactInfoList = contactInfoList;
    }
}
