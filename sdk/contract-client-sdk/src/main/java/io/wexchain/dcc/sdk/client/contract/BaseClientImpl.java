package io.wexchain.dcc.sdk.client.contract;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;

import io.wexchain.dcc.sdk.client.rest.RestClient;
import io.wexchain.dcc.sdk.service.UploadParam;

/**
 * readAndWriteFunction
 * 
 * @author shwh1
 *
 */
public class BaseClientImpl extends RestClient {

	protected URI createSubFunctionUri(String subFunctionPath) {
		return URI.create(basePath + subPath + subFunctionPath);
	}

	public String upload(URI uri, UploadParam uploadParam) {

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("ticket", uploadParam.getTicket());
		form.add("signMessage", uploadParam.getSignMessage());

		String s = restTemplate.postForObject(uri, form, String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, String.class);

		ResultResponse<String> resultResponse;
		try {
			resultResponse = objectMapper.readValue(s, javaType);
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResult();
	}

	public <T> T querySingleResult(URI uri, JavaType resultType, Pair<String, ?>... pairs) {
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, resultType);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
		for (Pair<String, ?> pair : pairs) {
			builder = builder.queryParam(pair.getKey(), pair.getValue());
		}

		String s = restTemplate.getForObject(builder.build().toUri(), String.class);

		ResultResponse<T> resultResponse;
		try {
			resultResponse = objectMapper.readValue(s, javaType);
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}

		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResult();
	}

	public <T> List<T> queryListResult(URI uri, JavaType elementType, Pair<String, ?>... pairs) throws IOException {
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ListResultResponse.class, elementType);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
		for (Pair<String, ?> pair : pairs) {
			builder = builder.queryParam(pair.getKey(), pair.getValue());
		}
		String s = restTemplate.getForObject(builder.build().toUri(), String.class);

		ListResultResponse<T> resultResponse = objectMapper.readValue(s, javaType);

		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResultList();
	}

}
