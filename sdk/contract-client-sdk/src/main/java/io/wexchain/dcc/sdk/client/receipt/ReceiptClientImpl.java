package io.wexchain.dcc.sdk.client.receipt;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;

import io.wexchain.dcc.sdk.client.rest.RestClient;

public class ReceiptClientImpl extends RestClient implements ReceiptClient {
	{
		subPath = "/receipt/1";
	}

	@Override
	public boolean hasReceipt(String transactionHash, String emitter) throws IOException {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(basePath + subPath + "/hasReceipt")
				.queryParam("txHash", transactionHash);
		if (StringUtils.isNotBlank(emitter)) {
			uriComponentsBuilder.queryParam("emitter", emitter);
		}
		URI uri = uriComponentsBuilder.build().toUri();
		String s = restTemplate.getForObject(uri, String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, Boolean.class);

		ResultResponse<Boolean> resultResponse = objectMapper.readValue(s, javaType);

		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResult();
	}

	@Override
	public boolean hasReceipt(String transactionHash) throws IOException {
		return hasReceipt(transactionHash, null);
	}

	@Override
	public ReceiptResult gasReceiptResult(String transactionHash, String emitter) throws IOException {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromHttpUrl(basePath + subPath + "/getReceiptResult").queryParam("txHash", transactionHash);
		if (StringUtils.isNotBlank(emitter)) {
			uriComponentsBuilder.queryParam("emitter", emitter);
		}
		URI uri = uriComponentsBuilder.build().toUri();
		String s = restTemplate.getForObject(uri, String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, ReceiptResult.class);

		ResultResponse<ReceiptResult> resultResponse = objectMapper.readValue(s, javaType);

		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResult();
	}

}
