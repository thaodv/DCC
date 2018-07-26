package io.wexchain.cryptoasset.loan.service;

import io.wexchain.cryptoasset.loan.api.ApplyRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanOrderPageRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CryptoAssetLoanService {

	LoanOrder advance(Long orderId);

	void advanceByApplyIdAsync(String applyId);

	LoanOrder getLoanOrder(Long orderId);

	Optional<LoanOrder> getLoanOrderByChainOrderIdNullable(Long chainOrderId);

	Optional<LoanOrder> getLoanOrderByOrderIndexNullable(OrderIndex index);

	LoanOrder getLoanOrderByChainOrderId(Long chainOrderId);

	LoanOrder getLoanOrderByOrderIndex(OrderIndex index);

	void cancel(OrderIndex index);

	LoanOrder advanceByChainOrderId(Long chainOrderId);

	LoanOrder getLoanOrderByApplyId(String applyId);

	LoanOrder apply(ApplyRequest request);

	Page<LoanOrder> queryLoanOrderPage(QueryLoanOrderPageRequest request);

	LoanOrder confirmRepayment(OrderIndex index);

	RepaymentBill queryRepaymentBill(OrderIndex index);

	List<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest);

    Integer queryYesterdayDeliverCount();
}
