package io.wexchain.passport.gateway.ctrlr.cert.dcc;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder;
import io.wexchain.passport.gateway.ctrlr.commons.SignMessageRequest;
import io.wexchain.passport.gateway.ctrlr.ticket.TicketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RequestMapping("/dcc/cert/1")
public class CertController {

	@Autowired
	protected TicketController ticketController;

	protected CertServiceProxyFactory certServiceProxyFactory;

	@RequestMapping(value = "/getAbi", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getAbi(@RequestParam @NotBlank String business) throws IOException {
		return ResultResponseUtils.successResultResponse(certServiceProxyFactory.getCertProxy(business).getAbi());
	}

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress(@RequestParam @NotBlank String business) throws IOException {
		return ResultResponseUtils.successResultResponse(certServiceProxyFactory.getCertProxy(business).getContractAddress());
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> apply(@RequestParam @NotBlank String business, @NotNull @Valid SignMessageRequest signMessageRequest)
			throws IOException {
		ticketController.verifyChallenge(signMessageRequest);
		String txHash = certServiceProxyFactory.getCertProxy(business).apply(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/pass", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> pass(@RequestParam @NotBlank String business, @NotBlank String signMessage) throws IOException {
		String pass = certServiceProxyFactory.getCertProxy(business).pass(signMessage);
		return ResultResponseUtils.successResultResponse(pass);
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> reject(@RequestParam @NotBlank String business, @NotBlank String signMessage) throws IOException {
		String reject = certServiceProxyFactory.getCertProxy(business).reject(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/revoke", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> revoke(@RequestParam @NotBlank String business, @NotBlank String signMessage) throws IOException {
		String revoke = certServiceProxyFactory.getCertProxy(business).revoke(signMessage);
		return ResultResponseUtils.successResultResponse(revoke);
	}

	@RequestMapping(value = "/getData", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertData(@RequestParam @NotBlank String business, @RequestParam @NotBlank String address) throws IOException {
		CertData idCertData = certServiceProxyFactory.getCertProxy(business).getData(address);
		return ResultResponseUtils.successResultResponse(idCertData);
	}

	@RequestMapping(value = "/getDataAt", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertDataAt(@RequestParam @NotBlank String business, @RequestParam @NotBlank String address,
			@RequestParam @NotBlank Long blockNumber) throws IOException {
		CertData getCertItem = certServiceProxyFactory.getCertProxy(business).getDataAt(address, blockNumber);
		return ResultResponseUtils.successResultResponse(getCertItem);
	}

	@RequestMapping(value = "/getOrderByTx", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertOrder> getCertOrderByTx(@RequestParam @NotBlank String business, @RequestParam @NotBlank String txHash) throws IOException {
		CertOrder idCertOrder = certServiceProxyFactory.getCertProxy(business).getOrder(txHash);
		return ResultResponseUtils.successResultResponse(idCertOrder);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertOrder> getCertOrder(@RequestParam @NotBlank String business, @RequestParam @NotBlank Long orderId) throws IOException {
		CertOrder idCertOrder = certServiceProxyFactory.getCertProxy(business).getOrder(orderId);
		return ResultResponseUtils.successResultResponse(idCertOrder);
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

	@Required
	public void setCertServiceProxyFactory(CertServiceProxyFactory certServiceProxyFactory) {
		this.certServiceProxyFactory = certServiceProxyFactory;
	}

}
