package io.wexchain.cryptoasset.loan.service.function.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferOrderListener {

	private static final Logger logger = LoggerFactory.getLogger(TransferOrderListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CryptoAssetLoanService cryptoAssetLoanService;

	@RabbitListener(queues = "${rabbitmq.queue.loanOrder}")
	public void received(TransferOrder transferOrder) {
		try {
			logger.info("Receive transfer order message:{}" ,objectMapper.writeValueAsString(transferOrder));
			cryptoAssetLoanService.handleTransferOrder(transferOrder);
		} catch (Exception e) {
			logger.error("Handle message fail", e);
		}

	}
}
