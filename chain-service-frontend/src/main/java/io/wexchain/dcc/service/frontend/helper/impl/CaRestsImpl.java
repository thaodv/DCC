package io.wexchain.dcc.service.frontend.helper.impl;


import com.fasterxml.jackson.databind.JavaType;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.helper.CaRests;
import io.wexchain.dcc.service.frontend.utils.Rests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CaRestsImpl implements CaRests {
	private static final Logger logger = LoggerFactory.getLogger(CaRestsImpl.class);

	@Value("${gateway.url.prefix}")
	private String gatewayUrlPrefix;

	@Override
	public byte[] getPubKey(String address) {
		ResultResponse<byte[]> resultResponse = null;
		String json = Rests.REST_TEMPLATE.getForObject(UriComponentsBuilder
						.fromHttpUrl(gatewayUrlPrefix + "/ca/1/getPubKey").queryParam("address",address).build().toUri(),
				String.class);

		JavaType javaType = Rests.TYPE_FACTORY.constructParametricType(ResultResponse.class, byte[].class);
		try {

			resultResponse = Rests.OBJECT_MAPPER.readValue(json, javaType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.GET_PUK_FAIL);
		return resultResponse.getResult();
	}
}
