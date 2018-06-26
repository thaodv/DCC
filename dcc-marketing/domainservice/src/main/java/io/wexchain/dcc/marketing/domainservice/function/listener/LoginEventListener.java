package io.wexchain.dcc.marketing.domainservice.function.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandlers;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginEventListener {

	private static final Logger logger = LoggerFactory.getLogger(LoginEventListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MiningEventHandlers handlers;

	@RabbitListener(queues = "${queue.login}")
	public void received(LoginEvent loginEvent) {
		logger.trace("接收登录事件通知loginEvent:{}", loginEvent);
		try {
			logger.info("收到登录事件消息: {}" ,objectMapper.writeValueAsString(loginEvent));
			for (MiningEventHandler handler : handlers.getLoginHandlerList()) {
				if (handler.canHandle(loginEvent)) {
					handler.handle(loginEvent);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

}
