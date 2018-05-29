package io.wexchain.passport.gateway.ctrlr.receipt;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.passport.gateway.service.contract.ReceiptResult;
import io.wexchain.passport.gateway.service.contract.Web3jProxy;

@RequestMapping("/receipt/1")
@RestController
public class ReceiptControllers {

	@Resource(name = "web3jProxy")
	private Web3jProxy web3jProxy;

	@RequestMapping(value = "/hasReceipt", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<Boolean> hasReceipt(@RequestParam @NotBlank String txHash,
			@RequestParam(required = false) String txFrom) throws IOException {
		ReceiptResult receiptResult = web3jProxy.getReceiptResult(txHash, txFrom);
		return ResultResponseUtils.successResultResponse(receiptResult.isHasReceipt());
	}

	@RequestMapping(value = "/getReceiptResult", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<ReceiptResult> getReceiptResult(@RequestParam @NotBlank String txHash,
			@RequestParam(required = false) String txFrom) throws IOException {
		ReceiptResult receiptResult = web3jProxy.getReceiptResult(txHash, txFrom);
		return ResultResponseUtils.successResultResponse(receiptResult);
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
