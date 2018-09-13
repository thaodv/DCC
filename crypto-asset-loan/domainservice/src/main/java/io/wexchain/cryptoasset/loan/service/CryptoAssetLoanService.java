package io.wexchain.cryptoasset.loan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import io.wexchain.cryptoasset.loan.api.*;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import org.springframework.data.domain.Page;

import com.wexmarket.topia.commons.pagination.Pagination;

import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;

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

	Pagination<LoanReport> queryLoanReportByIdentity(IdentityRequest identityRequest);

	AuditingOrder getAuditingOrder(Long orderId);

    AuditingOrder getAuditingOrderNullable(Long orderId);

    void handleTransferOrder(TransferOrder transferOrder);


    BigDecimal getTotalDeliverAmount(DeliverAmountRequest request);

	List<Long> addBorrowerAddress();
}
