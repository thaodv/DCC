package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.cryptoasset.loan.api.ConfirmRepaymentRequest;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanInterestRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderDetailVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderVo;
import io.wexchain.dcc.service.frontend.model.vo.RepaymentBillVo;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface LoanService {

    LoanOrder getLastOrder(MemberDetails memberDetails);

    void apply(LoanCreditApplyRequest loanCreditApplyRequest, MemberDetails memberDetails);

    Pagination<LoanOrderVo> queryLoanOrderPage(PageParam pageParam, Long memberId);

    LoanOrderDetailVo getLoanOrderByChainOrderId(Long chainOrderId);

    /**
     * 确认还款
     */
    void confirmRepayment(ConfirmRepaymentRequest request);

    /**
     * 查询还款账单
     */
    RepaymentBillVo queryRepaymentBill(Long chainOrderId);

    BigDecimal getLoanInterest(LoanInterestRequest loanInterestRequest);
}
