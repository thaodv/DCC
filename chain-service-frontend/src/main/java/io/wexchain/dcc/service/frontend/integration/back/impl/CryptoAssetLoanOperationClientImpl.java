package io.wexchain.dcc.service.frontend.integration.back.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.*;
import io.wexchain.cryptoasset.loan.api.*;
import io.wexchain.cryptoasset.loan.api.model.LoanOrder;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.dcc.marketing.api.facade.ActivityFacade;
import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.back.CryptoAssetLoanOperationClient;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.ActivityOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class CryptoAssetLoanOperationClientImpl implements CryptoAssetLoanOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "cryptoAssetLoanFacade")
    private IntegrationProxy<CryptoAssetLoanFacade> cryptoAssetLoanFacade;

    @Override
    public LoanOrder apply(ApplyRequest request) {
        ResultResponse<LoanOrder> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().apply(request), logger, "进件", request);
        if(!(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()))){
            throw new ErrorCodeException(FrontendErrorCode.LOAN_APPLY_FAIL.name(),resultResponse.getMessage());
        }
        return resultResponse.getResult();
    }

    @Override
    public Pagination<LoanOrder> queryLoanOrderPage(QueryLoanOrderPageRequest queryLoanOrderPageRequest) {
        ResultResponse<Pagination<LoanOrder>> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().queryLoanOrderPage(queryLoanOrderPageRequest), logger, "分页查询", queryLoanOrderPageRequest);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.QUERY_LOAN_ORDER_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public LoanOrder getLoanOrderByChainOrderId(OrderIndex index) {
        ResultResponse<LoanOrder> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().getLoanOrderByChainOrderId(index), logger, "查询", index);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.GET_LOAN_ORDER_DETAIL_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public LoanOrder confirmRepayment(OrderIndex index) {
        ResultResponse<LoanOrder> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().confirmRepayment(index), logger, "确认还款", index);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.CONFIRM_REPAYMENT_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public RepaymentBill queryRepaymentBill(OrderIndex index) {
        ResultResponse<RepaymentBill> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().queryRepaymentBill(index), logger, "查询还款订单", index);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.QUERY_LOAN_ORDER_BILL_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public void cancel(OrderIndex index) {
        BaseResponse response = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().cancel(index), logger, "取消订单", index);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == response.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(response.getBusinessCode()), FrontendErrorCode.CANCEL_LOAN_ORDER_FAIL, response.getMessage());
    }

    @Override
    public LoanOrder advance(Long chainOrderId) {
        ResultResponse<LoanOrder> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().advance(chainOrderId), logger, "推进", chainOrderId);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.ADVANCE_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public List<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest) {
        ListResultResponse<LoanReport> resultResponse = ExecuteTemplate.execute(() -> cryptoAssetLoanFacade.buildInst().queryLoanReport(queryLoanReportRequest), logger, "查询借贷报告", queryLoanReportRequest);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.QUERY_LOAN_ORDER_FAIL, resultResponse.getMessage());
        return resultResponse.getResultList();
    }

}
