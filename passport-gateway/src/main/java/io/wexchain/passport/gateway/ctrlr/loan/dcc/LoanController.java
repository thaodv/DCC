package io.wexchain.passport.gateway.ctrlr.loan.dcc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.data.rpc.PageTransformer;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.juzix.contract.loan.dcc.LoanOrder;
import io.wexchain.juzix.contract.loan.dcc.LoanOrderUpdatedEvent;
import io.wexchain.passport.gateway.ctrlr.commons.SignMessageRequest;
import io.wexchain.passport.gateway.ctrlr.ticket.TicketController;
import io.wexchain.passport.gateway.service.loan.dcc.LoanServiceProxy;

@RestController
@RequestMapping("/dcc/loan/1")
public class LoanController {

	@Autowired
	protected TicketController ticketController;

	@Autowired
	protected LoanServiceProxy loanServiceProxy;

	@RequestMapping(value = "/getAbi", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getAbi() throws IOException {
		return ResultResponseUtils.successResultResponse(loanServiceProxy.getAbi());
	}

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress() throws IOException {
		return ResultResponseUtils.successResultResponse(loanServiceProxy.getContractAddress());
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> apply(@NotNull @Valid SignMessageRequest signMessageRequest) throws IOException {
		ticketController.verifyChallenge(signMessageRequest);
		String txHash = loanServiceProxy.apply(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> cancel(@NotBlank String signMessage) throws IOException {
		String txHash = loanServiceProxy.cancel(signMessage);
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> audit(@NotBlank String signMessage) throws IOException {
		String txHash = loanServiceProxy.audit(signMessage);
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> reject(@NotBlank String signMessage) throws IOException {
		String reject = loanServiceProxy.reject(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/approve", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> approve(@NotBlank String signMessage) throws IOException {
		String approve = loanServiceProxy.approve(signMessage);
		return ResultResponseUtils.successResultResponse(approve);
	}

	@RequestMapping(value = "/deliver", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> deliver(@NotBlank String signMessage) throws IOException {
		String reject = loanServiceProxy.deliver(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/deliverFailure", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> deliverFailure(@NotBlank String signMessage) throws IOException {
		String deposit = loanServiceProxy.deliverFailure(signMessage);
		return ResultResponseUtils.successResultResponse(deposit);
	}

	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> receive(@NotBlank String signMessage) throws IOException {
		String deposit = loanServiceProxy.receive(signMessage);
		return ResultResponseUtils.successResultResponse(deposit);
	}

	@RequestMapping(value = "/confirmRepay", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> confirmRepay(@NotBlank String signMessage) throws IOException {
		String recordRepay = loanServiceProxy.confirmRepay(signMessage);
		return ResultResponseUtils.successResultResponse(recordRepay);
	}

	@RequestMapping(value = "/updateRepayDigest", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> updateRepayDigest(@NotBlank String signMessage) throws IOException {
		String recordRepay = loanServiceProxy.updateRepayDigest(signMessage);
		return ResultResponseUtils.successResultResponse(recordRepay);
	}

	@RequestMapping(value = "/getOrderByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<LoanOrder> getCertOrderByTx(@RequestParam @NotBlank String txHash) throws IOException {
		List<LoanOrder> loanOrders = loanServiceProxy.getOrder(txHash);
		return ListResultResponseUtils.successListResultResponse(loanOrders);
	}

	@RequestMapping(value = "/getOrderUpdatedEvents", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<LoanOrderUpdatedEvent> getOrderUpdatedEvents(@RequestParam @NotBlank String txHash)
			throws IOException {
		List<LoanOrderUpdatedEvent> orderUpdatedEvents = loanServiceProxy.getOrderUpdatedEvents(txHash);
		return ListResultResponseUtils.successListResultResponse(orderUpdatedEvents);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<LoanOrder> getCertOrder(@RequestParam @NotBlank Long orderId) throws IOException {
		LoanOrder loanOrder = loanServiceProxy.getOrder(orderId);
		return ResultResponseUtils.successResultResponse(loanOrder);
	}

	@RequestMapping(value = "/queryOrderPageByBorrowIndex", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<Pagination<LoanOrder>> queryOrderPageByBorrowIndex(
			@NotNull @Valid QueryOrderByAddress queryOrderByAddress) throws IOException {
		Page<LoanOrder> queryOrderPageByBorrowIndex = loanServiceProxy.queryOrderPageByBorrowIndex(queryOrderByAddress);
		Pagination<LoanOrder> transform = PageTransformer.transform(queryOrderPageByBorrowIndex);
		return ResultResponseUtils.successResultResponse(transform);
	}

	@RequestMapping(value = "/getOrderArrayLengthByBorrowerIndex", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigInteger> getOrderArrayLengthByBorrowerIndex(@RequestParam @NotBlank String address)
			throws IOException {
		BigInteger length = loanServiceProxy.getOrderArrayLengthByBorrowerIndex(address);
		return ResultResponseUtils.successResultResponse(length);
	}

	@RequestMapping(value = "/getMinFee", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigInteger> getMinFee() throws IOException {
		BigInteger minFee = loanServiceProxy.getMinFee();
		return ResultResponseUtils.successResultResponse(minFee);
	}

	@ExceptionHandler(ErrorCodeException.class)
	@ResponseBody
	public BaseResponse conflict(ErrorCodeException e) {
		return BaseResponseUtils.errorCodeExceptionResponse(e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse others(Exception e) {
		return BaseResponseUtils.exceptionBaseResponse(e);
	}

}
