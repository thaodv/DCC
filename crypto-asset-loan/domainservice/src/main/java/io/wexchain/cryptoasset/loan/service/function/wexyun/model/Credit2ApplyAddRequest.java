package io.wexchain.cryptoasset.loan.service.function.wexyun.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weihui.finance.common.credit2.enums.ApplySourceType;
import com.wexyun.open.api.anotation.jsr303.annotation.Length;
import com.wexyun.open.api.anotation.jsr303.annotation.NotBlank;
import com.wexyun.open.api.anotation.jsr303.annotation.NotNull;
import com.wexyun.open.api.anotation.jsr303.annotation.Size;
import com.wexyun.open.api.constants.ApiServicesNames;
import com.wexyun.open.api.domain.credit2.ContactInfo;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.domain.credit2.LoanMaterial;
import com.wexyun.open.api.enums.CertType;
import com.wexyun.open.api.enums.DurationType;
import com.wexyun.open.api.enums.credit2.MemberIdType;
import com.wexyun.open.api.request.BaseRequest;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import com.wexyun.open.api.util.json.JsonUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 信贷2.0进件请求参数
 * </p>
 *
 * @author huangfeitao
 * @version $Id: CreditApplyAddRequest.java, v 0.1 2016年7月28日 下午1:24:35 huangfeitao Exp $
 */
public class Credit2ApplyAddRequest extends BaseRequest {

    /**
     * 进件类型
     */
    @NotNull(message = "进件类型不能为空")
    private ApplySourceType applySourceType;
    /**
     * 借款人ID 与 债权人ID 必填一个
     */
    @Size(max = 32, message = "借款人ID长度校验失败，要求0-32位")
    private String          borrowerId;
    /**
     * 债权人ID
     */
    @Size(max = 32, message = "债权人ID长度校验失败，要求0-32位")
    private String          loanerId;

    /**
     * 商家ID(代理商的id)
     */
    @Size(max = 32, message = "商家ID长度校验失败，最多32位")
    private String          agentId;
    /**
     * 借款人姓名
     */
  //  @NotNull(message = "借款人姓名不能为空")
    @Size(max = 50, message = "借款人姓名长度校验失败，要求1-50位")
    private String          borrowName;
    /**
     * 贷款类型
     */
    @NotBlank(message = "贷款类型不能为空")
    private String          loanType;
    /**
     * 借款用途
     */
    @NotNull(message = "借款用途不能为空")
    @Size(max = 50, message = "借款用途长度校验失败，要求1-50位")
    private String          borrowUse;
    /**
     * 借款金额
     */
    @NotNull(message = "借款金额不能为空")
    private BigDecimal      borrowAmount;
    /**
     * 借款期限
     */
    @NotNull(message = "借款期限不能为空")
    private Integer         borrowDuration;
    /**
     * 借款期限单位
     */
    @NotNull(message = "借款期限单位不能为空")
    private DurationType    durationType;

    /**
     * 证件号
     */
    @Size(max = 32, message = "证件号长度校验失败，最多32位")
    private String          certNo;
    /**
     * 证件类型
     */
    private CertType        certType = CertType.ID_CARD;
    /**
     * 借款人手机号
     */
 //   @NotNull(message = "借款人手机号不能为空")
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
    private Date            applyDate;

    /**
     * 进件数据来源方标识(进件接口调用方的描述.自定义)
     */
    @NotNull(message = "进件数据来源方标识")
    @Length(min = 3, max = 32, message = "进件数据来源方标识长度校验失败,要求3~32位")
    private String          sourceIdentity;

    /**
     * 外部申请ID 新增时填上作幂等
     */
    @Size(max = 32, message = "外部申请ID长度校验失败，最多32位")
    private String          externalApplyId;

    /**
     * 收款方ID
     */
    @Size(max = 32, message = "收款方ID长度校验失败，最多32位")
    private String          receiveMemberId;
    /**
     * 还款方ID
     */
    @Size(max = 32, message = "还款方ID长度校验失败，最多32位")
    private String          repaymentMemberId;

    /**
     * 扩展字段信息（借款模板信息）
     */
    private String          itemDetailMap;

    /**
     * 贷款资料列表
     */
    private String          loanMaterialList;

    /**
     * 联系人信息列表
     */
    private String          contactInfoList;
    /**
     * 会员ID类型 1、云金融会员ID 2、新浪支付资金托管UID 默认云金融会员
     */
    private MemberIdType         memberIdType;

    public MemberIdType getMemberIdType() {
        return memberIdType;
    }

    public void setMemberIdType(MemberIdType memberIdType) {
        this.memberIdType = memberIdType;
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

    public BigDecimal getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(BigDecimal borrowAmount) {
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

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getSourceIdentity() {
        return sourceIdentity;
    }

    public void setSourceIdentity(String sourceIdentity) {
        this.sourceIdentity = sourceIdentity;
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

    public void setItemDetailMap(String itemDetailMap) {
        this.itemDetailMap = itemDetailMap;
    }

    public void setItemDetailMap(Map<String, String> itemDetailMap) {
        this.itemDetailMap = JsonUtil.toJson(itemDetailMap);
    }

    public String getItemDetailMap() {
        return itemDetailMap;
    }

    public String getLoanMaterialList() {
        return loanMaterialList;
    }

    public void setLoanMaterialList(String loanMaterialList) {
        this.loanMaterialList = loanMaterialList;
    }

    public String getContactInfoList() {
        return contactInfoList;
    }

    public void setContactInfoList(String contactInfoList) {
        this.contactInfoList = contactInfoList;
    }

    public void setLoanMaterialList(List<LoanMaterial> loanMaterialList) {
        this.loanMaterialList = JsonUtil.toJson(loanMaterialList);
    }

    public void setContactInfoList(List<ContactInfo> contactInfoList) {
        this.contactInfoList = JsonUtil.toJson(contactInfoList);
    }

    @Override
    public String getService() {
        return ApiServicesNames.CREDIT2_APPLY_ADD;
    }

    @Override
    public TypeReference<? extends BaseResponse> getTypeReference() {
        return new TypeReference<QueryResponse4Single<Credit2ApplyAddResult>>() {
        };
    }
}
