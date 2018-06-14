package io.wexchain.dcc.marketing.domainservice.processor.order.rewarddelivery.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.dcc.marketing.api.constant.RewardDeliveryStatus;
import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.RetryableCommand;
import io.wexchain.dcc.marketing.domain.RewardDelivery;
import io.wexchain.dcc.marketing.domain.RewardLog;
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.command.CommandIndex;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandHelper;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandTemplate;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewarddelivery.RewardDeliveryInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewarddelivery.RewardDeliveryTrigger;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import io.wexchain.dcc.marketing.repository.RewardLogRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;


public class DeliverAdvancer extends AbstractAdvancer<RewardDelivery, RewardDeliveryInstruction, RewardDeliveryTrigger> {

	{
		availableStatus = RewardDeliveryStatus.CREATED;
	}

	@Autowired
	private RewardDeliveryRepository rewardDeliveryRepository;

	@Autowired
	private CahFunction cahFunction;

	@Autowired
	private RetryableCommandTemplate retryableCommandTemplate;

	@Autowired
	private RewardLogRepository rewardLogRepository;

	@Override
	public AdvancedResult<RewardDelivery, RewardDeliveryTrigger> advance(
			RewardDelivery rewardDelivery, RewardDeliveryInstruction instruction, Object message) {

		RetryableCommand collectCommand = executeRedeem(rewardDelivery);

		if (RetryableCommandHelper.isSuccess(collectCommand)) {
			return new AdvancedResult<>(new TriggerBehavior<>(RewardDeliveryTrigger.DELIVER, this::saveRewardLog));
		}
		return null;
	}


	private RetryableCommand executeRedeem(RewardDelivery rewardDelivery) {
		CommandIndex commandIndex = new CommandIndex(RedeemToken.TYPE_REF, rewardDelivery.getId(), "DELIVER");
		return retryableCommandTemplate.execute(commandIndex,
				ci -> Validate.notNull(rewardDeliveryRepository.lockById(ci.getParentId())), null,
				command -> {
					if (RetryableCommandHelper.isCreated(command)) {
						TransferOrder transferOrder = cahFunction.redeem(
								String.valueOf(command.getId()),
								rewardDelivery.getAmount().toBigInteger(),
								rewardDelivery.getRewardRound().getActivity().getSupplierAddress(),
								rewardDelivery.getBeneficiaryAddress());
						if (transferOrder.getStatus() == TransferOrderStatus.SUCCESS) {
							command.setMemo(transferOrder.getAmount() + "");
							return GeneralCommandStatus.SUCCESS.name();
						} else if (transferOrder.getStatus() == TransferOrderStatus.FAILURE) {
							return GeneralCommandStatus.FAILURE.name();
						}
					}
					return command.getStatus();
				});
	}

	private void saveRewardLog(RewardDelivery rewardDelivery) {
		RewardLog log = new RewardLog();
		log.setAmount(rewardDelivery.getAmount());
		log.setActivityId(rewardDelivery.getRewardRound().getActivity().getId());
		log.setReceiverAddress(rewardDelivery.getBeneficiaryAddress());
		rewardLogRepository.save(log);
	}
}