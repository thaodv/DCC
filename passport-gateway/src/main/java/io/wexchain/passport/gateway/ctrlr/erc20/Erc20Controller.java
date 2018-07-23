package io.wexchain.passport.gateway.ctrlr.erc20;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.wexchain.passport.gateway.execute.Erc20Executor;
import io.wexchain.passport.gateway.service.contract.SignMessageValidator;
import io.wexchain.passport.gateway.utils.Rest;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.passport.gateway.service.erc20.Erc20Validation;
import io.wexchain.passport.gateway.service.erc20.FTCServiceProxy;

@RestController
@RequestMapping("/erc20/{path}/1")
public class Erc20Controller {

	private static final Logger logger = LoggerFactory.getLogger(Erc20Controller.class);

	@Value("${juzix.url}")
	private String nodeUrl;

	@Autowired
	private Erc20Validation erc20Validation;

	@Autowired
	private FTCServiceProxy ftcServiceProxy;

	@RequestMapping(value = "/web3", method = {RequestMethod.POST,RequestMethod.OPTIONS})
	@ResponseBody
	public void web3(@PathVariable("path") String path, @RequestBody Map<String, Object> body, ServletResponse response) throws IOException, URISyntaxException {
		response.setContentType("application/json;charset=UTF-8");
		erc20Validation.validateWithBusiness(body,path);
		ResponseEntity<String> exchange= Erc20Executor.execute(body, nodeUrl, path);
		response.getWriter().print(exchange.getBody());

	}

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress(@PathVariable("path") String path) {
		return ResultResponseUtils.successResultResponse(erc20Validation.getContractAddress(path));
	}

	@RequestMapping(value = "/getFeeRate", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigDecimal> getGetFeeRate(@PathVariable("path") String path) {
		return ResultResponseUtils.successResultResponse(ftcServiceProxy.getFeeRate());
	}

	@RequestMapping(value = "/getExpectedFee", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<BigInteger> getExpectedFee(@PathVariable("path") String path, @RequestParam @NotBlank String sender, @RequestParam @NotNull BigInteger amount) {
		return ResultResponseUtils.successResultResponse(ftcServiceProxy.getExpectedFee(sender,amount));
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