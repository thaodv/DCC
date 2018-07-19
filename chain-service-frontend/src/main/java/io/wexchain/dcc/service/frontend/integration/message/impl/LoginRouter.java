package io.wexchain.dcc.service.frontend.integration.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wexchain.dcc.service.frontend.integration.message.MessageRouter;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginRouter implements MessageRouter<LoginEvent> {

	private static final Logger logger = LoggerFactory.getLogger(LoginRouter.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Resource(name = "loginExchange")
	private Exchange exchange;

	@Override
	public void route(LoginEvent message) {
		try {
			logger.info("登录完成发送通知:{}", objectMapper.writeValueAsString(message));
			amqpTemplate.convertAndSend(exchange.getName(), "", message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
}
