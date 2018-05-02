package io.wexchain.dcc.sdk.client.rest;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestClient {
	protected RestTemplate restTemplate;

	protected ObjectMapper objectMapper;

	protected String basePath;

	protected String subPath;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setSubPath(String subPath) {
		this.subPath = subPath;
	}


}
