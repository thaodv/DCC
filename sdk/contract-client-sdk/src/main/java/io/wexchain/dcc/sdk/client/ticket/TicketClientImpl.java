package io.wexchain.dcc.sdk.client.ticket;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.Validate;
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;

import io.wexchain.dcc.sdk.client.model.Ticket;
import io.wexchain.dcc.sdk.client.rest.RestClient;

public class TicketClientImpl extends RestClient implements TicketClient {
	{
		subPath = "/ticket/1";
	}

	@Override
	public String getTicket() throws IOException {
		String s = restTemplate.postForObject(URI.create(basePath + subPath + "/getTicket"),
				new LinkedMultiValueMap<>(), String.class);
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructParametricType(ResultResponse.class, Ticket.class);

		ResultResponse<Ticket> resultResponse = objectMapper.readValue(s, javaType);

		Validate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
				&& BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()));

		return resultResponse.getResult().getTicket();
	}

}
