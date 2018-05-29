package io.wexchain.passport.gateway.ctrlr.cert.dcc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder3;
import io.wexchain.juzix.contract.cert.CertOrderUpdatedEvent;
import io.wexchain.passport.gateway.ctrlr.commons.SignMessageRequest;
import io.wexchain.passport.gateway.ctrlr.ticket.TicketController;
import io.wexchain.passport.gateway.service.cert.dcc.DccCertServiceProxy;

@RequestMapping("/dcc/cert/1")
public class DccCertController {

	@Autowired
	protected TicketController ticketController;

	protected DccCertServiceProxyFactory certServiceProxyFactory;

	@RequestMapping(value = "/getAbi", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getAbi(@RequestParam @NotBlank String business) throws IOException {
		return ResultResponseUtils.successResultResponse(certServiceProxyFactory.getCertProxy(business).getAbi());
	}

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress(@RequestParam @NotBlank String business) throws IOException {
		return ResultResponseUtils
				.successResultResponse(certServiceProxyFactory.getCertProxy(business).getContractAddress());
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> apply(@RequestParam @NotBlank String business,
			@NotNull @Valid SignMessageRequest signMessageRequest) throws IOException {
		ticketController.verifyChallenge(signMessageRequest);
		DccCertServiceProxy certProxy = certServiceProxyFactory.getCertProxy(business);
		String txHash = certProxy.apply(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/pass", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> pass(@RequestParam @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String pass = certServiceProxyFactory.getCertProxy(business).pass(signMessage);
		return ResultResponseUtils.successResultResponse(pass);
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> reject(@RequestParam @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String reject = certServiceProxyFactory.getCertProxy(business).reject(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/revoke", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> revoke(@RequestParam @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String revoke = certServiceProxyFactory.getCertProxy(business).revoke(signMessage);
		return ResultResponseUtils.successResultResponse(revoke);
	}

	@RequestMapping(value = "/getData", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertData(@RequestParam @NotBlank String business,
			@RequestParam @NotBlank String address) throws IOException {
		CertData idCertData = certServiceProxyFactory.getCertProxy(business).getData(address);
		return ResultResponseUtils.successResultResponse(idCertData);
	}

	@RequestMapping(value = "/getDataAt", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertDataAt(@RequestParam @NotBlank String business,
			@RequestParam @NotBlank String address, @RequestParam @NotBlank Long blockNumber) throws IOException {
		CertData getCertItem = certServiceProxyFactory.getCertProxy(business).getDataAt(address, blockNumber);
		return ResultResponseUtils.successResultResponse(getCertItem);
	}

	@RequestMapping(value = "/getOrdersByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<CertOrder3> getCertOrdersByTx(@RequestParam @NotBlank String business,
			@RequestParam @NotBlank String txHash) throws IOException {
		List<CertOrder3> orders = certServiceProxyFactory.getCertProxy(business).getOrders(txHash);
		return ListResultResponseUtils.successListResultResponse(orders);
	}

	@RequestMapping(value = "/getOrderUpdatedEventsByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<CertOrderUpdatedEvent> getOrderUpdatedEventsByTx(@RequestParam @NotBlank String business,
			@RequestParam @NotBlank String txHash) throws IOException {
		List<CertOrderUpdatedEvent> orderUpdatedEvents = certServiceProxyFactory.getCertProxy(business)
				.getOrderUpdatedEvents(txHash);
		return ListResultResponseUtils.successListResultResponse(orderUpdatedEvents);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertOrder3> getCertOrder(@RequestParam @NotBlank String business,
			@RequestParam @NotBlank Long orderId) throws IOException {
		CertOrder3 idCertOrder = certServiceProxyFactory.getCertProxy(business).getOrder(orderId);
		return ResultResponseUtils.successResultResponse(idCertOrder);
	}

	@RequestMapping(value = "/getExpectedFee", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigInteger> getExpectedFee(@RequestParam @NotBlank String business) throws IOException {
		BigInteger expectedFee = certServiceProxyFactory.getCertProxy(business).getExpectedFee();
		return ResultResponseUtils.successResultResponse(expectedFee);
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
	public void setCertServiceProxyFactory(DccCertServiceProxyFactory certServiceProxyFactory) {
		this.certServiceProxyFactory = certServiceProxyFactory;
	}

}
