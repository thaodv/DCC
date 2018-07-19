package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.pagination.SortPageParam;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.cryptoasset.loan.api.ApplyRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanOrderPageRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.LoanType;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import io.wexchain.dcc.service.frontend.common.constants.LoanExtParamConstants;
import io.wexchain.dcc.service.frontend.common.convertor.LoanConvertor;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.integration.back.CryptoAssetLoanOperationClient;
import io.wexchain.dcc.service.frontend.integration.wexyun.FileOperationClient;
import io.wexchain.dcc.service.frontend.model.Period;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanInterestRequest;
import io.wexchain.dcc.service.frontend.model.vo.*;
import io.wexchain.dcc.service.frontend.service.dcc.loanProduct.LoanProductService;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import io.wexchain.dcc.service.frontend.utils.AddressUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;


/**
 * LoanCreditServiceImpl
 *
 */
@Service(value = "dccLoanService")
public class LoanServiceImpl implements LoanService,FrontendWebConstants,LoanExtParamConstants{

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    private io.wexchain.dcc.loan.sdk.service.LoanService loanService;

    @Autowired
    private LoanProductService loanProductService;

    @Value(value = "${app.identity}")
    private String appIdentity;

    @Autowired
    private FileOperationClient fileOperationClient;

    @Autowired
    private CryptoAssetLoanOperationClient cryptoAssetLoanOperationClient;

    @Autowired
    private MemberService memberService;


    @Override
    public LoanOrder getLastOrder(MemberDetails memberDetails) {
        try {
            return loanService.getLastOrder(memberDetails.getUsername());
        } catch (IOException e) {
            logger.error("查询最后借款订单失败：", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(LoanCreditApplyRequest loanCreditApplyRequest,MemberDetails memberDetails) {

        LoanProductVo loanProductVo = loanProductService.getLoanProductVo(loanCreditApplyRequest.getLoanProductId());
        List<BigDecimal> volumeOptionList = loanProductVo.getVolumeOptionList();
        List<Period> loanPeriodList = loanProductVo.getLoanPeriodList();

        boolean volumeValidateResult = false;
        for (BigDecimal volumeOption : volumeOptionList) {
            if(volumeOption.compareTo(loanCreditApplyRequest.getBorrowAmount())==0){
                volumeValidateResult = true;
                break;
            }
        }
        ErrorCodeValidate.isTrue(volumeValidateResult, FrontendErrorCode.LOAN_APPLY_FAIL, "不合法的借款金额");
        ErrorCodeValidate.isTrue(borrowDuration(loanPeriodList,loanCreditApplyRequest.getBorrowDuration()), FrontendErrorCode.LOAN_APPLY_FAIL, "不合法的借款期限");
        ApplyRequest applyRequest = new ApplyRequest();
        applyRequest.setIndex(new OrderIndex(loanCreditApplyRequest.getOrderId(),memberDetails.getId().toString()));
        applyRequest.setLoanProductId(loanCreditApplyRequest.getLoanProductId().toString());
        applyRequest.setAmount(loanCreditApplyRequest.getBorrowAmount());
        applyRequest.setBorrowDuration(loanCreditApplyRequest.getBorrowDuration());
        applyRequest.setBorrowName(loanCreditApplyRequest.getBorrowName());
        applyRequest.setDurationUnit(loanCreditApplyRequest.getDurationUnit());
        applyRequest.setCertNo(loanCreditApplyRequest.getCertNo());
        applyRequest.setMobile(loanCreditApplyRequest.getMobile());
        applyRequest.setBankCardNo(loanCreditApplyRequest.getBankCard());
        applyRequest.setBankMobile(loanCreditApplyRequest.getBankMobile());
        applyRequest.setApplyDate(loanCreditApplyRequest.getApplyDate());
        applyRequest.setLoanType(loanProductVo.getLoanType());
        applyRequest.setExpectAnnualRate(loanProductVo.getLoanRate().toString());
        applyRequest.setAssetCode(loanProductVo.getCurrency().getSymbol());
        applyRequest.setRepayMode("TOGETHER");
        applyRequest.setAppIdentity(appIdentity);
        applyRequest.setRepaymentCycleNo(loanProductVo.getRepayCyclesNo());
        applyRequest.setFacePic(loanCreditApplyRequest.getPersonalPhoto());
        applyRequest.setIdCardBackPic(loanCreditApplyRequest.getBackPhoto());
        applyRequest.setIdCardFrontPic(loanCreditApplyRequest.getFrontPhoto());


        Map<String, String> stringStringHashMap = new HashMap<>();
        applyRequest.setApplicationDigestClearingText(stringStringHashMap);

        io.wexchain.cryptoasset.loan.api.model.LoanOrder apply = cryptoAssetLoanOperationClient.apply(applyRequest);
        ErrorCodeValidate.isTrue(StringUtils.isBlank(apply.getFailCode()), FrontendErrorCode.LOAN_APPLY_FAIL, apply.getFailMessage());
    }

    @Override
    public Pagination<LoanOrderVo> queryLoanOrderPage(PageParam pageParam, Long memberId) {
        QueryLoanOrderPageRequest queryLoanOrderPageRequest = new QueryLoanOrderPageRequest();
        queryLoanOrderPageRequest.setMemberId(memberId.toString());
        queryLoanOrderPageRequest.setSortPageParam(new SortPageParam(pageParam.getNumber(),pageParam.getSize()));
        List<LoanOrderStatus> excludeStatusList = new ArrayList<>();
        excludeStatusList.add(LoanOrderStatus.CANCELLED);
        excludeStatusList.add(LoanOrderStatus.FAILURE);
        queryLoanOrderPageRequest.setExcludeStatusList(excludeStatusList);
        Pagination<io.wexchain.cryptoasset.loan.api.model.LoanOrder> loanOrderPagination = cryptoAssetLoanOperationClient.queryLoanOrderPage(queryLoanOrderPageRequest);

        List<LoanOrderVo> loanOrderVoList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(loanOrderPagination.getItems())){
            for (io.wexchain.cryptoasset.loan.api.model.LoanOrder loanOrder : loanOrderPagination.getItems()) {
                LoanProductVo loanProductVo = loanProductService.getLoanProductVo(Long.parseLong(loanOrder.getExtParam().get(LOAN_PRODUCT_ID)));
                LoanOrderVo loanOrderVo = new LoanOrderVo();
                setBaseLoanOrderInfo(loanOrderVo,loanOrder,loanProductVo);
                loanOrderVoList.add(loanOrderVo);
            }
        }
        Pagination<LoanOrderVo> loanOrderVoPagination = new Pagination<>();
        loanOrderVoPagination.setItems(loanOrderVoList);
        loanOrderVoPagination.setSortPageParam(loanOrderPagination.getSortPageParam());
        loanOrderVoPagination.setTotalElements(loanOrderPagination.getTotalElements());
        loanOrderVoPagination.setTotalPages(loanOrderPagination.getTotalPages());
        return loanOrderVoPagination;
    }

    @Override
    public LoanOrderDetailVo getLoanOrderByChainOrderId(OrderIndex index) {
        io.wexchain.cryptoasset.loan.api.model.LoanOrder loanOrder = cryptoAssetLoanOperationClient.getLoanOrderByChainOrderId(index);
        LoanProductVo loanProductVo = loanProductService.getLoanProductVo(Long.parseLong(loanOrder.getExtParam().get(LOAN_PRODUCT_ID)));
        LoanOrderDetailVo vo = new LoanOrderDetailVo();
        vo.setReceiverAddress(loanOrder.getReceiverAddress());
        vo.setAllowRepayPermit(loanProductVo.isRepayPermit());
        vo.setLoanRate(loanProductVo.getLoanRate());

        Map<String, String> extParam = loanOrder.getExtParam();
        setBaseLoanOrderInfo(vo,loanOrder,loanProductVo);
        setLoanOrderDetailInfo(vo,  extParam);

        return vo;
    }

    @Override
    public void confirmRepayment(OrderIndex index) {
        cryptoAssetLoanOperationClient.confirmRepayment(index);
    }

    @Override
    public RepaymentBillVo queryRepaymentBill(OrderIndex index) {
        RepaymentBill repaymentBill = cryptoAssetLoanOperationClient.queryRepaymentBill(index);
        return new RepaymentBillVo(repaymentBill);
    }

    @Override
    public BigDecimal getLoanInterest(LoanInterestRequest loanInterestRequest) {
        LoanProductVo loanProductVo = loanProductService.getLoanProductVo(loanInterestRequest.getProductId());
        BigDecimal loanInterest = loanInterestRequest.getAmount().multiply(loanInterestRequest.getLoanPeriodValue()).multiply(loanProductVo.getLoanRate()).divide(new BigDecimal("365"),4,BigDecimal.ROUND_HALF_UP);
        return loanInterest;
    }

    @Override
    public void cancel(OrderIndex index) {
        cryptoAssetLoanOperationClient.cancel(index);
    }

    @Override
    public void downloadAgreement(OutputStream outputStream, OrderIndex index) throws IOException {
        io.wexchain.cryptoasset.loan.api.model.LoanOrder loanOrderByChainOrderId = cryptoAssetLoanOperationClient.getLoanOrderByChainOrderId(index);
        Map<String, String> extParam = loanOrderByChainOrderId.getExtParam();
        String filePath = extParam.get(AGREEMENT_PATH);
        ErrorCodeValidate.isTrue(StringUtils.isNotBlank(filePath),FrontendErrorCode.FILE_NOT_EXIST,"文件不存在");
        InputStream inputStream = fileOperationClient.download(filePath);
        ErrorCodeValidate.isTrue(inputStream != null,FrontendErrorCode.FILE_NOT_EXIST,"文件不存在");
        //写文件
        try {
            int b;
            while((b=inputStream.read())!= -1)
            {
                outputStream.write(b);
            }
        }finally {
            inputStream.close();
            outputStream.close();
        }
    }

    @Override
    public io.wexchain.cryptoasset.loan.api.model.LoanOrder advance(Long chainOrderId) {
        return cryptoAssetLoanOperationClient.advance(chainOrderId);
    }

    @Override
    public List<LoanReportVo> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest) {
        List<LoanReport> loanReports = cryptoAssetLoanOperationClient.queryLoanReport(queryLoanReportRequest);
        List<LoanReportVo> voList = new ArrayList<>();
        for (LoanReport loanReport : loanReports) {
            LoanReportVo vo = LoanConvertor.convert(loanReport);
            if(loanReport.getChainOrderId() == null){
                continue;
            }
            if(loanReport.getLoanType() == LoanType.LOAN){
                LoanProductVo loanProductVo = loanProductService.getLoanProductVo(Long.parseLong(loanReport.getLoanProductId()));
                vo.setLenderName(loanProductVo.getLender().getName());
            }
            if(loanReport.getLoanType() == LoanType.MORTGAGE){
                vo.setLenderName(loanReport.getDeliverDept());
            }
            voList.add(vo);

        }
        return voList;
    }

    @Override
    public List<LoanReportVo> introQueryLoanReport(String address) {
        Member member = memberService.getByIdentity(address);

        if(member != null){
            QueryLoanReportRequest queryLoanReportRequest = new QueryLoanReportRequest();
            queryLoanReportRequest.setMemberId(member.getMemberId());
            queryLoanReportRequest.setAddress(address);
            return queryLoanReport(queryLoanReportRequest);
        }
        return null;
    }

    private static void setBaseLoanOrderInfo(LoanOrderVo loanOrderVo , io.wexchain.cryptoasset.loan.api.model.LoanOrder loanOrder,LoanProductVo loanProductVo ){
        loanOrderVo.setOrderId(loanOrder.getChainOrderId());
        loanOrderVo.setApplyDate(new Date(Long.parseLong(loanOrder.getExtParam().get(APPLY_DATE))));
        loanOrderVo.setStatus(loanOrder.getStatus());
        loanOrderVo.setCurrency(loanProductVo.getCurrency());
        loanOrderVo.setLender(loanProductVo.getLender());
        loanOrderVo.setAmount(loanOrder.getAmount());
        loanOrderVo.setProductLogoUrl(loanProductVo.getLogoUrl());
    }
    private static void setLoanOrderDetailInfo(LoanOrderDetailVo vo , Map<String, String> extParam){

        if(StringUtils.isNotBlank(extParam.getOrDefault(BORROW_DURATION_UNIT,null))){
            vo.setDurationUnit(extParam.get(BORROW_DURATION_UNIT));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(LOAN_FEE,null))){
            vo.setFee(new BigDecimal(extParam.get(LOAN_FEE)));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(DELIVER_DATE,null))){
            vo.setDeliverDate(new Date(Long.parseLong(extParam.get(DELIVER_DATE))));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(REPAY_DATE,null))){
            vo.setRepayDate(new Date(Long.parseLong(extParam.get(REPAY_DATE))));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(EXPECT_REPAY_DATE,null))){
            vo.setExpectRepayDate(new Date(Long.parseLong(extParam.get(EXPECT_REPAY_DATE))));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(LOAN_INTEREST,null))){
            vo.setLoanInterest(new BigDecimal(extParam.get(LOAN_INTEREST)).setScale(4,BigDecimal.ROUND_HALF_UP));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(EXPECT_LOAN_INTEREST,null))){
            vo.setExpectLoanInterest(new BigDecimal(extParam.get(EXPECT_LOAN_INTEREST)).setScale(4,BigDecimal.ROUND_HALF_UP));
        }
        if(StringUtils.isNotBlank(extParam.getOrDefault(BORROW_DURATION,null))){
            vo.setBorrowDuration(new Integer(extParam.get(BORROW_DURATION)));
        }
        if(vo.getStatus() == LoanOrderStatus.DELIVERED && vo.getRepayDate() != null){
            DateTime dateTime = new DateTime(vo.getRepayDate().getTime());
            if(dateTime.isAfterNow()){
                vo.setEarlyRepayAvailable(true);
            }
        }

    }

    private static boolean borrowDuration(List<Period> loanPeriodList , int borrowDuration){
        for (Period period : loanPeriodList) {
            if(period.getValue() == borrowDuration){
                return true;
            }
        }
        return false;
    }
}
