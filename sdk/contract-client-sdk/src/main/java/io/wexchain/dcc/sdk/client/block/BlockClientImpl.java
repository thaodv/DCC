package io.wexchain.dcc.sdk.client.block;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;

import io.wexchain.dcc.sdk.client.rest.RestClient;

public class BlockClientImpl extends RestClient implements BlockClient {
	{
		subPath = "/block/1";
	}

	@Override
	public List<String> getTxHashList(Long blockNumber) throws IOException {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromHttpUrl(basePath + subPath + "/getTxHashList").queryParam("blockNumber", blockNumber);
		URI uri = uriComponentsBuilder.build().toUri();
		String s = restTemplate.getForObject(uri, String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ListResultResponse.class, String.class);
		ListResultResponse<String> resultResponse = objectMapper.readValue(s, javaType);
		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));
		return resultResponse.getResultList();
	}

	@Override
	public Long getBlockNumber() throws IOException {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromHttpUrl(basePath + subPath + "/getBlockNumber");
		URI uri = uriComponentsBuilder.build().toUri();
		String s = restTemplate.getForObject(uri, String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, Long.class);
		ResultResponse<Long> resultResponse = objectMapper.readValue(s, javaType);
		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));
		return resultResponse.getResult();
	}

}
