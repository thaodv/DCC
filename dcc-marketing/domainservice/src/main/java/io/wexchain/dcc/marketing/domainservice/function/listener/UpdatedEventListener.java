package io.wexchain.dcc.marketing.domainservice.function.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandlers;
import io.wexchain.notify.domain.dcc.OrderUpdatedEvent;

@Service
public class UpdatedEventListener {
	private static final Logger logger = LoggerFactory.getLogger(UpdatedEventListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MiningEventHandlers handlers;

	@RabbitListener(queues = "${queue.updated}")
	public void received(OrderUpdatedEvent orderUpdatedEvent) {
		logger.trace("接收订单状态变更事件通知orderUpdatedEvent:{}", orderUpdatedEvent);
		try {
			for (MiningEventHandler handler : handlers.getLoanHandlerList()) {
				if (handler.canHandle(orderUpdatedEvent)) {
					handler.handle(orderUpdatedEvent);
				}
			}
			logger.info("接收订单状态变更事件消息: {}" ,objectMapper.writeValueAsString(orderUpdatedEvent));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
