package io.wexchain.dcc.marketing.domainservice.function.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandlers;
import io.wexchain.notify.domain.dcc.VerifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifiedEventListener {

	private static final Logger logger = LoggerFactory.getLogger(VerifiedEventListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MiningEventHandlers handlers;

	@RabbitListener(queues = "${queue.verified}")
	public void received(VerifiedEvent verifiedEvent) {
		logger.trace("接收增信背书通过事件通知verifiedEvent:{}", verifiedEvent);
		try {
			for (MiningEventHandler handler : handlers.getCertHandlerList()) {
				if (handler.canHandle(verifiedEvent)) {
					handler.handle(verifiedEvent);
				}
			}
			logger.info("接收增信背书通过事件消息: {}" ,objectMapper.writeValueAsString(verifiedEvent));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
