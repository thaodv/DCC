package io.wexchain.cryptoasset.loan.service.function.wexyun.impl;

import com.alibaba.fastjson.JSON;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.weihui.basic.lang.common.domain.OperationEnvironment;
import com.weihui.finance.common.credit2.enums.ApplySourceType;
import com.weihui.finance.contract.api.request.GeneratPDFFileRequest;
import com.weihui.finance.contract.api.response.GeneratPDFFileResponse;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.WexyunApiClient;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.domain.file.DownloadFileInfo;
import com.wexyun.open.api.domain.file.UploadFileInfo;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.domain.regular.agreement.DebtAgreement;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.enums.AuthVerifyStatus;
import com.wexyun.open.api.enums.RepaymentType;
import com.wexyun.open.api.enums.UploadFileType;
import com.wexyun.open.api.enums.YN;
import com.wexyun.open.api.enums.credit2.MemberIdType;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.BaseFileDownLoadRequest;
import com.wexyun.open.api.request.common.CommonFileUploadRequest;
import com.wexyun.open.api.request.loan.RegularAgreementVerifyRequest;
import com.wexyun.open.api.request.loan.RegularPrepaymentBillGetRequest;
import com.wexyun.open.api.request.loan.RegularRepaymentPlanListRequest;
import com.wexyun.open.api.request.member.MemberInfoGetByIdRequest;
import com.wexyun.open.api.request.trade.TradeRePaymentAddRequest;
import com.wexyun.open.api.response.*;
import io.wexchain.cryptoasset.loan.common.exception.RpcException;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyAddRequest;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyGetRequest;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.DebtAgreementInfo;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * WexyunLoanClientImpl
 *
 * @author zhengpeng
 */
@Service
public class WexyunLoanClientImpl implements WexyunLoanClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WexyunApiClient wexyunApiClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wexyun.contract.remoteUrl}")
    private String wexyunContractRemoteUrl;

    @Value("${wexyun.api.partnerId}")
    private String partnerId;

    @Value("${wexyun.contract.templateId:1}")
    private String contractTemplateId;

    private static final String BORROW_USE = "数字资产借贷";

    @Override
    public QueryResponse4Single<Credit2Apply> getApplyOrder(String applyId) {
        Credit2ApplyGetRequest request = new Credit2ApplyGetRequest();
        request.setApplyId(applyId);
        try {
            return wexyunApiClient.call(request);
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public Member getMemberInfoById(String memberId) {
        MemberInfoGetByIdRequest request = new MemberInfoGetByIdRequest();
        request.setRequireIdentitys(YN.Y);
        request.setMemberId(memberId);
        try {
            QueryResponse4Single<Member> response =  wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getContent();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public String getAddressById(String memberId) {
        Member member = getMemberInfoById(memberId);
        if(member != null && CollectionUtils.isNotEmpty(member.getIdentitys())){
            return member.getIdentitys().get(0).getIdentity();
        }
        return null;
    }

    @Override
    public Credit2Apply getApplyOrder2(String applyId) {
        Credit2ApplyGetRequest request = new Credit2ApplyGetRequest();
        request.setApplyId(applyId);
        try {
            QueryResponse4Single<Credit2Apply> response =  wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getContent();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public QueryResponse4Single<Credit2ApplyAddResult> apply(Credit2ApplyAddRequest credit2ApplyAddRequest) {
        credit2ApplyAddRequest.setApplySourceType(ApplySourceType.TOKENCOIN);
        credit2ApplyAddRequest.setBorrowUse(BORROW_USE);
        credit2ApplyAddRequest.setMemberIdType(MemberIdType.WEX);
        try {
            return wexyunApiClient.call(credit2ApplyAddRequest);
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public RepaymentPlan queryFirstRepaymentPlan(String applyId) {
        List<RepaymentPlan> repaymentPlans = queryRepaymentPlan(applyId);

        return repaymentPlans.get(0);
    }

    @Override
    public List<RepaymentPlan> queryRepaymentPlan(String applyId) {
        RegularRepaymentPlanListRequest request = new RegularRepaymentPlanListRequest();

        request.setApplyId(Long.valueOf(applyId));
        try {
            QueryResponse4Batch<RepaymentPlan> response = wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getItems();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public RegularPrepaymentBill queryRegularPrepaymentBill(String applyId) {
        RegularPrepaymentBillGetRequest request = new RegularPrepaymentBillGetRequest();
        request.setRepaymentDate(new Date());
        request.setApplyId(applyId);
        try {
            QueryResponse4Single<RegularPrepaymentBill> response = wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getContent();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }


    @Override
    public void repay(String billId, String memberId, BigDecimal amount, RepaymentType repaymentType) {
        logger.info("billId:{}, memberId:{}, amount:{}, repaymentType:{}", billId, memberId, amount, repaymentType);
        TradeRePaymentAddRequest request = new TradeRePaymentAddRequest();
        request.setBillId(billId);
        request.setMemberId(memberId);
        request.setAmount(amount);
        request.setRepaymentType(repaymentType);
        request.setNeedPaymethod(YN.N);
        request.setExtension("tradeNoPay=true");
        request.setPortion(BigDecimal.ZERO);
        try {
            TradeOrder4PayResponse response = wexyunApiClient.call(request);
            logger.info("Repayment response:{}", JSON.toJSONString(response));
            if (!response.isSuccess()) {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public GeneratPDFFileResponse generateAgreement(LoanOrder loanOrder,  Credit2Apply applyOrder, RepaymentPlan repaymentPlan) {
        GeneratPDFFileRequest request = new GeneratPDFFileRequest();
        request.setPartnerId(partnerId);
        request.setFileName("contract.pdf");
        request.setTemplateId(contractTemplateId);
        request.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        request.setSubmitTime(new Date());
        request.setMemberId(repaymentPlan.getMemberId());

        // 填充信息
        Map<String, String> params = new HashMap<>();
        DateTime date = new DateTime(repaymentPlan.getBillStartDate());
        request.setFillContext("{}");
        try {
            DebtAgreementInfo debtAgreementInfo = buildDebtAgreementInfo(loanOrder, applyOrder, repaymentPlan);
            request.setFillContext(JsonFlattener.flatten(JSON.toJSONString(debtAgreementInfo)));
        } catch (Exception e) {
            logger.info("Build agreement fail", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("request",  JSON.toJSONString(request));
        map.add("environment",  JSON.toJSONString(new OperationEnvironment()));

        HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<>(map, headers);

        String url = wexyunContractRemoteUrl.concat("/restful/contract/ext/contract/generate");
        GeneratPDFFileResponse response = restTemplate.postForObject(url, request1, GeneratPDFFileResponse.class);
        if (!response.isSuccess()) {
            throw new ErrorCodeException(response.getErrorCode(), response.getResultMessage());
        }

        return response;
    }


    private DebtAgreementInfo buildDebtAgreementInfo(LoanOrder loanOrder,  Credit2Apply applyOrder, RepaymentPlan repaymentPlan) {

        DebtAgreement debtAgreement = applyOrder.getDebtAgreement();
        DateTime billStartDate = new DateTime(repaymentPlan.getBillStartDate());
        DateTime lastRepaymentTime = new DateTime(repaymentPlan.getLastRepaymentTime());

        DebtAgreementInfo info = new DebtAgreementInfo();

        // sign date
        info.setSignDate(DebtAgreementInfo.DateInfo.of(billStartDate));

        // person info
        info.setCreditor(new DebtAgreementInfo.PersonInfo("朱旭日", "330681199202075018",
                "0x8df0be313758ab901b2d084bb19d6e14c7cccdfc"));
        info.setDebtor(new DebtAgreementInfo.PersonInfo(applyOrder.getBorrowName(), applyOrder.getCertNo(),
                loanOrder.getReceiverAddress()));

        // detail
        info.setAssetCode(loanOrder.getAssetCode());
        info.setAmount(loanOrder.getAmount().setScale(4, RoundingMode.DOWN));
        info.setAnnualizedRatePercent(debtAgreement.getExpectAnnuRate().multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN));
        info.setRepaymentAmount(AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount()));
        info.setRepaymentDate(DebtAgreementInfo.DateInfo.of(lastRepaymentTime));
        info.setRepaymentType("到期还本付息");
        info.setBorrowDateFrom(DebtAgreementInfo.DateInfo.of(billStartDate));
        info.setBorrowDateTo(DebtAgreementInfo.DateInfo.of(lastRepaymentTime));

        // rate
        info.setFee(new BigDecimal(loanOrder.getExtParam().get(LoanOrderExtParamKey.LOAN_FEE)));
        if (debtAgreement.getOverdueFineAnnuRate() != null) {
            info.setOverdueRatePercent(debtAgreement.getOverdueFineAnnuRate().multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN));
        }
        info.setEarlyRepaymentRatePercent(debtAgreement.getFineInterestValue().multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN));

        //info.setAgreementId(loanOrder.getId().toString());

        return info;
    }

    @Override
    public DownloadFileInfo downloadFile(String filePath) {
        try {
            BaseFileDownLoadRequest request = new BaseFileDownLoadRequest();
            request.setFilePath(filePath);
            request.setPartnerId(partnerId);
            return wexyunApiClient.downLoad(request);
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public String uploadImageFile(File file) {
        try {
            CommonFileUploadRequest request = new CommonFileUploadRequest();
            request.setFileName(file.getName());
            request.setFileType(UploadFileType.BORROW_APTITUDE);
            Map<String, UploadFileInfo> files = new HashMap<>();
            UploadFileInfo f = new UploadFileInfo();
            f.setFile(file);
            f.setFileName(file.getName());
            // 根据文件类型来灵活指定,参考com.wexyun.open.api.enums.MIME.java
            f.setMime("IMAGE_JPEG_JPEG");
            files.put("file_content", f);
            request.setFiles(files);

            QueryResponse4Single<CommonFileUploadResponse> response = wexyunApiClient.call(request);
            if (!response.isSuccess()) {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
            logger.info("Upload result: {}", JSON.toJSONString(response));
            return response.getContent().getFilePath();
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void verifyAgreement(String applyId, String loanType) {
        try {
            RegularAgreementVerifyRequest request = new RegularAgreementVerifyRequest();
            request.setVerifyStatus(AuthVerifyStatus.PASS);
            request.setApplyId(applyId);
            Map<String, String> extension = new HashMap<>(2);
            extension.put("APPLY_ID", applyId);
            extension.put("LOAN_TYPE", loanType);
            request.setExtension(JSON.toJSONString(extension));
            logger.info("loan type:{}", loanType);
            BaseResponse response = wexyunApiClient.call(request);
            if (!response.isSuccess()) {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
            logger.info("Verify agreement result: {}", JSON.toJSONString(response));
        } catch (WexyunClientException e) {
            throw new RpcException(e);
        }

    }
}
