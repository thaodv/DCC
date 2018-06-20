package io.wexchain.dcc.marketing.domainservice.processor.candy.advancer;

import org.springframework.beans.factory.annotation.Autowired;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.domain.Candy;
import io.wexchain.dcc.marketing.domain.RetryableCommand;
import io.wexchain.dcc.marketing.domain.RewardLog;
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.command.CommandIndex;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandHelper;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandTemplate;
import io.wexchain.dcc.marketing.domainservice.processor.candy.CandyInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.candy.CandyTrigger;
import io.wexchain.dcc.marketing.repository.CandyRepository;
import io.wexchain.dcc.marketing.repository.RewardLogRepository;

public class DeliveryAdvancer extends AbstractAdvancer<Candy, CandyInstruction, CandyTrigger> {
	{
		availableStatus = CandyStatus.PICKED;
	}
	@Autowired
	private RetryableCommandTemplate retryableCommandTemplate;

	@Autowired
	private CandyRepository candyRepository;

	@Autowired
	private RewardLogRepository rewardLogRepository;

	@Autowired
	private CahFunction cahFunction;

	@Override
	public AdvancedResult<Candy, CandyTrigger> advance(Candy candy, CandyInstruction instruction, Object message) {
		RetryableCommand command = executeDelivery(candy);
		if (RetryableCommandHelper.isSuccess(command)) {
			return new AdvancedResult<>(new TriggerBehavior<>(CandyTrigger.DELIVERY, this::saveRewardLog));
		}
		return null;
	}

	private RetryableCommand executeDelivery(Candy candy) {
		CommandIndex commandIndex = new CommandIndex(Candy.TYPE_REF, candy.getId(), "DELIVERY");
		return retryableCommandTemplate.execute(commandIndex, ci -> candyRepository.lockById(ci.getParentId()).get(),
				null, command -> {
					if (RetryableCommandHelper.isCreated(command)) {
						TransferOrder transferOrder = cahFunction.transferDccJuzix(String.valueOf(command.getId()),
								candy.getAmount().toBigInteger(), candy.getPayer(), candy.getOwner());
						if (transferOrder.getStatus() == TransferOrderStatus.SUCCESS) {
							return GeneralCommandStatus.SUCCESS.name();
						} else if (transferOrder.getStatus() == TransferOrderStatus.FAILURE) {
							return GeneralCommandStatus.FAILURE.name();
						}
					}
					return command.getStatus();
				});
	}

	private void saveRewardLog(Candy candy) {
		RewardLog log = new RewardLog();
		log.setAmount(candy.getAmount());
		log.setActivityId(candy.getActivity().getId());
		log.setReceiverAddress(candy.getOwner());
		rewardLogRepository.save(log);
	}

}
