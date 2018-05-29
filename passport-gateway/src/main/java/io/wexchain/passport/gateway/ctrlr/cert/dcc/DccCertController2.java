package io.wexchain.passport.gateway.ctrlr.cert.dcc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
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

@RequestMapping("/dcc/cert/2/{business}")
@RestController
public class DccCertController2 {

	@Autowired
	protected TicketController ticketController;

	@Autowired
	protected DccCertServiceProxyFactory certServiceProxyFactory;

	@RequestMapping(value = "/getAbi", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getAbi(@PathVariable @NotBlank String business) throws IOException {
		return ResultResponseUtils
				.successResultResponse(certServiceProxyFactory.getCertProxy(business.toUpperCase()).getAbi());
	}

	private static Converter<String, String> caseFormatConverter = CaseFormat.LOWER_CAMEL
			.converterTo(CaseFormat.UPPER_UNDERSCORE);

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress(@PathVariable @NotBlank String business) throws IOException {
		return ResultResponseUtils.successResultResponse(
				certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business)).getContractAddress());
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> apply(@PathVariable @NotBlank String business,
			@NotNull @Valid SignMessageRequest signMessageRequest) throws IOException {
		ticketController.verifyChallenge(signMessageRequest);
		DccCertServiceProxy certProxy = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business));
		String txHash = certProxy.apply(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/pass", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> pass(@PathVariable @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String pass = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business)).pass(signMessage);
		return ResultResponseUtils.successResultResponse(pass);
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> reject(@PathVariable @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String reject = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business)).reject(signMessage);
		return ResultResponseUtils.successResultResponse(reject);
	}

	@RequestMapping(value = "/revoke", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> revoke(@PathVariable @NotBlank String business, @NotBlank String signMessage)
			throws IOException {
		String revoke = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business)).revoke(signMessage);
		return ResultResponseUtils.successResultResponse(revoke);
	}

	@RequestMapping(value = "/getData", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertData(@PathVariable @NotBlank String business,
			@RequestParam @NotBlank String address) throws IOException {
		CertData idCertData = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business))
				.getData(address);
		return ResultResponseUtils.successResultResponse(idCertData);
	}

	@RequestMapping(value = "/getDataAt", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertData> getCertDataAt(@PathVariable @NotBlank String business,
			@RequestParam @NotBlank String address, @RequestParam @NotBlank Long blockNumber) throws IOException {
		CertData getCertItem = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business))
				.getDataAt(address, blockNumber);
		return ResultResponseUtils.successResultResponse(getCertItem);
	}

	@RequestMapping(value = "/getOrdersByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<CertOrder3> getCertOrdersByTx(@PathVariable @NotBlank String business,
			@RequestParam @NotBlank String txHash) throws IOException {
		List<CertOrder3> orders = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business))
				.getOrders(txHash);
		return ListResultResponseUtils.successListResultResponse(orders);
	}

	@RequestMapping(value = "/getOrderUpdatedEventsByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<CertOrderUpdatedEvent> getOrderUpdatedEventsByTx(@PathVariable @NotBlank String business,
			@RequestParam @NotBlank String txHash) throws IOException {
		List<CertOrderUpdatedEvent> orderUpdatedEvents = certServiceProxyFactory
				.getCertProxy(caseFormatConverter.convert(business)).getOrderUpdatedEvents(txHash);
		return ListResultResponseUtils.successListResultResponse(orderUpdatedEvents);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<CertOrder3> getCertOrder(@PathVariable @NotBlank String business,
			@RequestParam @NotBlank Long orderId) throws IOException {
		CertOrder3 idCertOrder = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business))
				.getOrder(orderId);
		return ResultResponseUtils.successResultResponse(idCertOrder);
	}

	@RequestMapping(value = "/getExpectedFee", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigInteger> getExpectedFee(@PathVariable @NotBlank String business) throws IOException {
		BigInteger expectedFee = certServiceProxyFactory.getCertProxy(caseFormatConverter.convert(business))
				.getExpectedFee();
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

}
