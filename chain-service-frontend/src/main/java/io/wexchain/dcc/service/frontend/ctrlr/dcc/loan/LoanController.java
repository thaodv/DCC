package io.wexchain.dcc.service.frontend.ctrlr.dcc.loan;

import com.weihui.basic.lang.common.lang.StringUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.cryptoasset.loan.api.ConfirmRepaymentRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanInterestRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderDetailVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanReportVo;
import io.wexchain.dcc.service.frontend.model.vo.RepaymentBillVo;
import io.wexchain.dcc.service.frontend.service.dcc.loan.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


/**
 * LoanController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class LoanController extends SecurityBaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "dccLoanService")
    private LoanService dccLoanService;

    @PostMapping("/secure/loan/getLastOrder")
    public ResultResponse<LoanOrder> getLastOrder() {
        LoanOrder lastOrder = dccLoanService.getLastOrder(getMember());
        return ResultResponseUtils.successResultResponse(lastOrder);
    }

    @PostMapping("/secure/loan/apply")
    public BaseResponse apply(@Valid LoanCreditApplyRequest loanCreditApplyRequest, BindingResult bindingResult) {
        dccLoanService.apply(loanCreditApplyRequest,getMember());
        return BaseResponseUtils.successBaseResponse();
    }

    @PostMapping("/secure/loan/queryOrderPage")
    public ResultResponse<Pagination<LoanOrderVo>> queryOrderPage(@Valid PageParam loanCreditApplyRequest, BindingResult bindingResult) {
        Pagination<LoanOrderVo>  loanOrders = dccLoanService.queryLoanOrderPage(loanCreditApplyRequest,getMemberId());
        return ResultResponseUtils.successResultResponse(loanOrders);
    }

    @PostMapping("/secure/loan/getByChainOrderId")
    public ResultResponse<LoanOrderDetailVo> getLoanOrderByChainOrderId(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        OrderIndex index = new OrderIndex(chainOrderId,getMemberId().toString());
        LoanOrderDetailVo loanOrders = dccLoanService.getLoanOrderByChainOrderId(index);
        return ResultResponseUtils.successResultResponse(loanOrders);
    }

    @PostMapping("/secure/loan/confirmRepayment")
    public BaseResponse confirmRepayment(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        OrderIndex index  = new OrderIndex();
        index.setChainOrderId(chainOrderId);
        index.setMemberId(getMemberId().toString());
        dccLoanService.confirmRepayment(index);
        return BaseResponseUtils.successBaseResponse();
    }

    @PostMapping("/secure/loan/getRepaymentBill")
    public ResultResponse<RepaymentBillVo> getRepaymentBill(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        OrderIndex index = new OrderIndex(chainOrderId,getMemberId().toString());
        RepaymentBillVo repaymentBillVo = dccLoanService.queryRepaymentBill(index);
        return ResultResponseUtils.successResultResponse(repaymentBillVo);
    }

    @PostMapping("/loan/getLoanInterest")
    public ResultResponse<BigDecimal> getLoanInterest(@Valid LoanInterestRequest loanInterestRequest) {
        BigDecimal loanInterest = dccLoanService.getLoanInterest(loanInterestRequest);
        return ResultResponseUtils.successResultResponse(loanInterest);
    }

    @PostMapping("/loan/advance")
    public BaseResponse cancel(HttpServletRequest request, @NotNull(message = "链上订单号不能为空") Long chainOrderId) {

        String header = request.getHeader("x-advance");
        if(!"wjLFVSDjb3soKfaj".equals(header)){
            return BaseResponseUtils.codeBaseResponse(SystemCode.SUCCESS,"404","接口不存在");
        }
        io.wexchain.cryptoasset.loan.api.model.LoanOrder loanOrder = dccLoanService.advance(chainOrderId);
        return ResultResponseUtils.successResultResponse(loanOrder);
    }

    @PostMapping("/secure/loan/cancel")
    public BaseResponse advance(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        OrderIndex index = new OrderIndex(chainOrderId,getMemberId().toString());
        dccLoanService.cancel(index);
        return BaseResponseUtils.successBaseResponse();
    }

    @PostMapping("/secure/loan/queryReport")
    public ListResultResponse<LoanReportVo> queryLoanReport() {
        QueryLoanReportRequest queryLoanReportRequest = new QueryLoanReportRequest(getMemberId().toString(), getMember().getUsername());
        List<LoanReportVo> voList = dccLoanService.queryLoanReport(queryLoanReportRequest);
        return ListResultResponseUtils.successListResultResponse(voList);
    }

    @GetMapping("/secure/loan/download/agreement")
    public void downloadAgreement(@NotNull(message = "链上订单号不能为空") Long chainOrderId, HttpServletResponse response) {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=agreement.pdf");
        try {
            OrderIndex index = new OrderIndex(chainOrderId,getMemberId().toString());
            dccLoanService.downloadAgreement(response.getOutputStream(),index);
        } catch (IOException e) {
            logger.error("文件下载时错误:[{}]",e);
            throw new ErrorCodeException(FrontendErrorCode.DOWNLOAD_FAIL.name(),"文件下载异常");
        }
    }

}
