package io.wexchain.passport.gateway.ctrlr.loan.xiaoxinyong;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.juzix.contract.loan.xiaoxinyong.LoanOrderV1;
import io.wexchain.passport.gateway.ctrlr.commons.SignMessageRequest;
import io.wexchain.passport.gateway.ctrlr.ticket.TicketController;
import io.wexchain.passport.gateway.service.loan.xiaoxinyong.LoanServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/loan/1")
public class LoanControllerV1 {

	@Autowired
	protected TicketController ticketController;

	@Resource(name = "loanServiceProxyV1")
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
	public ResultResponse<String> apply(@NotNull @Valid SignMessageRequest signMessageRequest)
			throws IOException {
		ticketController.verifyChallenge(signMessageRequest);
		String txHash = loanServiceProxy.apply(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/approve", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> approve(@NotBlank String signMessage) throws IOException {
		String approve = loanServiceProxy.approve(signMessage);
		return ResultResponseUtils.successResultResponse(approve);
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> reject(@NotBlank String signMessage) throws IOException {
		String reject = loanServiceProxy.reject(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/deposit", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> deposit(@NotBlank String signMessage) throws IOException {
		String deposit = loanServiceProxy.deposit(signMessage);
		return ResultResponseUtils.successResultResponse(deposit);
	}

	@RequestMapping(value = "/recordRepay", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> recordRepay(@NotBlank String signMessage) throws IOException {
		String recordRepay = loanServiceProxy.recordRepay(signMessage);
		return ResultResponseUtils.successResultResponse(recordRepay);
	}

	@RequestMapping(value = "/payOff", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> payOff(@NotBlank String signMessage) throws IOException {
		String payOff = loanServiceProxy.payOff(signMessage);
		return ResultResponseUtils.successResultResponse(payOff);
	}

	@RequestMapping(value = "/forceUpdateStatus", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> forceUpdateStatus(@NotBlank String signMessage) throws IOException {
		String forceUpdateStatus = loanServiceProxy.forceUpdateStatus(signMessage);
		return ResultResponseUtils.successResultResponse(forceUpdateStatus);
	}

	@RequestMapping(value = "/getOrderByTx", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<LoanOrderV1> getCertOrderByTx(@RequestParam @NotBlank String txHash) throws IOException {
		LoanOrderV1 loanOrder = loanServiceProxy.getOrder(txHash);
		return ResultResponseUtils.successResultResponse(loanOrder);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<LoanOrderV1> getCertOrder(@RequestParam @NotBlank Long orderId) throws IOException {
		LoanOrderV1 loanOrder = loanServiceProxy.getOrder(orderId);
		return ResultResponseUtils.successResultResponse(loanOrder);
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
