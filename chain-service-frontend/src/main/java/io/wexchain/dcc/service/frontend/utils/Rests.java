package io.wexchain.dcc.service.frontend.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Rests {
	public static RestTemplate REST_TEMPLATE = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	static {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());

		REST_TEMPLATE.setMessageConverters(messageConverters);
	}
	public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public static TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();

	public static JavaType STR_VALUE_JAVA_TYPE = TYPE_FACTORY.constructParametricType(ResultResponse.class, String.class);

	public static JavaType BOOLEAN_VALUE_JAVA_TYPE = TYPE_FACTORY.constructParametricType(ResultResponse.class,
			Boolean.class);
	public static JavaType BYTE_ARRAY_VALUE_JAVA_TYPE = TYPE_FACTORY.constructParametricType(ResultResponse.class,
			byte[].class);

}
