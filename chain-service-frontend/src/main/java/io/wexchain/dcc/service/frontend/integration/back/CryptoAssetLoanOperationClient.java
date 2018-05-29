package io.wexchain.dcc.service.frontend.integration.back;


import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.cryptoasset.loan.api.ApplyRequest;
import io.wexchain.cryptoasset.loan.api.ConfirmRepaymentRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanOrderPageRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.model.LoanOrder;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;

import java.util.List;

public interface CryptoAssetLoanOperationClient {

    /**
     * 进件
     */
    LoanOrder apply(ApplyRequest request);

    /**
     * 分页查询借款订单
     */
    Pagination<LoanOrder> queryLoanOrderPage(QueryLoanOrderPageRequest queryLoanOrderPageRequest);

    /**
     * 查询
     */
    LoanOrder getLoanOrderByChainOrderId(OrderIndex index);

    /**
     * 确认还款
     */
    LoanOrder confirmRepayment(OrderIndex index);

    /**
     * 查询还款账单
     */
    RepaymentBill queryRepaymentBill(OrderIndex index);

    /**
     * 取消订单
     */
    void cancel(OrderIndex index);

    /**
     * 推进
     */
    LoanOrder advance(Long chainOrderId);

    /**
     * 查询借贷报告
     */
    List<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest);
}
