package io.wexchain.dcc.service.frontend.ctrlr.dcc.loan;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.cryptoasset.loan.api.ConfirmRepaymentRequest;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanInterestRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderDetailVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderVo;
import io.wexchain.dcc.service.frontend.model.vo.RepaymentBillVo;
import io.wexchain.dcc.service.frontend.service.dcc.loan.LoanService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * LoanController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class LoanController extends SecurityBaseController {

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
        LoanOrderDetailVo loanOrders = dccLoanService.getLoanOrderByChainOrderId(chainOrderId);
        return ResultResponseUtils.successResultResponse(loanOrders);
    }

    @PostMapping("/secure/loan/confirmRepayment")
    public BaseResponse confirmRepayment(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        ConfirmRepaymentRequest confirmRepaymentRequest = new ConfirmRepaymentRequest();
        confirmRepaymentRequest.setChainOrderId(chainOrderId);
        confirmRepaymentRequest.setMemberId(getMemberId().toString());
        dccLoanService.confirmRepayment(confirmRepaymentRequest);
        return BaseResponseUtils.successBaseResponse();
    }

    @PostMapping("/secure/loan/getRepaymentBill")
    public ResultResponse<RepaymentBillVo> getRepaymentBill(@NotNull(message = "链上订单号不能为空") Long chainOrderId) {
        RepaymentBillVo repaymentBillVo = dccLoanService.queryRepaymentBill(chainOrderId);
        return ResultResponseUtils.successResultResponse(repaymentBillVo);
    }

    @PostMapping("/loan/getLoanInterest")
    public ResultResponse<BigDecimal> getLoanInterest(@Valid LoanInterestRequest loanInterestRequest) {
        BigDecimal loanInterest = dccLoanService.getLoanInterest(loanInterestRequest);
        return ResultResponseUtils.successResultResponse(loanInterest);
    }

}
