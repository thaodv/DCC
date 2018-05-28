package io.wexchain.cryptoasset.loan.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.cryptoasset.loan.api.*;
import io.wexchain.cryptoasset.loan.api.model.LoanOrder;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.cryptoasset.loan.ext.service.helper.LoanAssetOrderResponseHelper;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * CryptoAssetLoanFacadeImpl
 *
 * @author zhengpeng
 */
@Component("cryptoAssetLoanFacade")
@Validated
public class CryptoAssetLoanFacadeImpl implements CryptoAssetLoanFacade {

	@Autowired
	private LoanAssetOrderResponseHelper loanAssetOrderResponseHelper;

	@Autowired
	private CryptoAssetLoanService cryptoAssetLoanService;

	@Override
	public ResultResponse<LoanOrder> advance(Long chainOrderId) {
		try {
			io.wexchain.cryptoasset.loan.domain.LoanOrder order = cryptoAssetLoanService
					.advanceByChainOrderId(chainOrderId);
			return loanAssetOrderResponseHelper.returnSuccess(order);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ResultResponse<LoanOrder> apply(ApplyRequest request) {
		try {
			io.wexchain.cryptoasset.loan.domain.LoanOrder order = cryptoAssetLoanService.apply(request);
			order.setExtParam(new HashMap<>(order.getExtParam()));
			return loanAssetOrderResponseHelper.returnSuccess(order);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ResultResponse<LoanOrder> confirmRepayment(OrderIndex index) {
		try {
			io.wexchain.cryptoasset.loan.domain.LoanOrder order = cryptoAssetLoanService.confirmRepayment(index);
			order.setExtParam(new HashMap<>(order.getExtParam()));
			return loanAssetOrderResponseHelper.returnSuccess(order);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public BaseResponse cancel(OrderIndex index) {
		try {
			cryptoAssetLoanService.cancel(index);
			return BaseResponseUtils.successBaseResponse();
		} catch (Exception e) {
			return BaseResponseUtils.exceptionBaseResponse(e);
		}
	}

	@Override
	public ResultResponse<LoanOrder> getLoanOrderByChainOrderId(OrderIndex index) {
		try {
			io.wexchain.cryptoasset.loan.domain.LoanOrder order = cryptoAssetLoanService
					.getLoanOrderByOrderIndex(index);
			order.setExtParam(new HashMap<>(order.getExtParam()));
			return loanAssetOrderResponseHelper.returnSuccess(order);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ResultResponse<Pagination<LoanOrder>> queryLoanOrderPage(QueryLoanOrderPageRequest request) {
		try {
			Page<io.wexchain.cryptoasset.loan.domain.LoanOrder> pageResult = cryptoAssetLoanService
					.queryLoanOrderPage(request);
			Optional.ofNullable(pageResult.getContent()).ifPresent(
					list -> list.forEach(loanOrder -> loanOrder.setExtParam(new HashMap<>(loanOrder.getExtParam()))));
			return loanAssetOrderResponseHelper.returnPageSuccess(pageResult);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ResultResponse<RepaymentBill> queryRepaymentBill(OrderIndex index) {
		try {
			RepaymentBill repaymentBill = cryptoAssetLoanService.queryRepaymentBill(index);
			return ResultResponseUtils.successResultResponse(repaymentBill);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ListResultResponse<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest) {
		try {
			List<LoanReport> loanReports = cryptoAssetLoanService.queryLoanReport(queryLoanReportRequest);
			return ListResultResponseUtils.successListResultResponse(loanReports);
		} catch (Exception e) {
			return ListResultResponseUtils.exceptionListResultResponse(e);
		}
	}

}
