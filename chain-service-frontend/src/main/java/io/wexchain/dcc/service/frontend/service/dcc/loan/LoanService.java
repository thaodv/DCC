package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.cryptoasset.loan.api.ConfirmRepaymentRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanInterestRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderDetailVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanOrderVo;
import io.wexchain.dcc.service.frontend.model.vo.LoanReportVo;
import io.wexchain.dcc.service.frontend.model.vo.RepaymentBillVo;

import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface LoanService {

    LoanOrder getLastOrder(MemberDetails memberDetails);

    void apply(LoanCreditApplyRequest loanCreditApplyRequest, MemberDetails memberDetails);

    Pagination<LoanOrderVo> queryLoanOrderPage(PageParam pageParam, Long memberId);

    LoanOrderDetailVo getLoanOrderByChainOrderId(OrderIndex index);

    /**
     * 确认还款
     */
    void confirmRepayment(OrderIndex index);

    /**
     * 查询还款账单
     */
    RepaymentBillVo queryRepaymentBill(OrderIndex index);

    BigDecimal getLoanInterest(LoanInterestRequest loanInterestRequest);

    /**
     * 取消订单
     */
    void cancel(OrderIndex index);
    /**
     * 下载合同
     */
    void downloadAgreement(OutputStream outputStream, OrderIndex index) throws IOException;

    /**
     * 推进
     */
    io.wexchain.cryptoasset.loan.api.model.LoanOrder advance(Long chainOrderId);

    /**
     * 查询借贷报告
     */
    List<LoanReportVo> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest);
}
