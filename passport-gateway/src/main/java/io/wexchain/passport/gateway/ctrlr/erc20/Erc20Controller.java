package io.wexchain.passport.gateway.ctrlr.erc20;

import com.alibaba.fastjson.JSONObject;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.passport.gateway.service.erc20.Erc20Validation;
import io.wexchain.passport.gateway.service.erc20.FTCServiceProxy;
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
import org.web3j.abi.datatypes.Address;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erc20/{path}/1")
public class Erc20Controller {

	public static RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	static {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		messageConverters.add(stringHttpMessageConverter);
		restTemplate.setMessageConverters(messageConverters);
	}
	
	@Value("${juzix.url}")
	private String nodeUrl;

	@Autowired
	private Erc20Validation erc20Validation;

	@Autowired
	private FTCServiceProxy ftcServiceProxy;

	@RequestMapping(value = "/web3", method = {RequestMethod.POST,RequestMethod.OPTIONS})
	@ResponseBody
	public Object web3(@PathVariable("path") String path, @RequestBody Map<String, Object> body) throws IOException, URISyntaxException {
		erc20Validation.validate(body,path);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("accept", MediaType.APPLICATION_JSON.toString());
		headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
		RequestEntity<String> requestEntity = new RequestEntity<String>(JSONObject.toJSONString(body), headers, HttpMethod.POST,
				new URI(nodeUrl));
		ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
		return JSONObject.parse(exchange.getBody());
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