package io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.advancer;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenStatus;
import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.RetryableCommand;
import io.wexchain.dcc.marketing.domain.RewardLog;
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.command.CommandIndex;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandHelper;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandTemplate;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenTrigger;
import io.wexchain.dcc.marketing.repository.RedeemTokenRepository;
import io.wexchain.dcc.marketing.repository.RewardLogRepository;


public class RedeemAdvancer extends AbstractAdvancer<RedeemToken, RedeemTokenInstruction, RedeemTokenTrigger> {
	{
		availableStatus = RedeemTokenStatus.DECIDED;
	}

	@Value("${contract.address.dcc}")
	private String dccTokenAddress;

	@Autowired
	private CahFunction cahFunction;

	@Autowired
	private RetryableCommandTemplate retryableCommandTemplate;

	@Autowired
	private RedeemTokenRepository redeemTokenRepository;

	@Autowired
	private RewardLogRepository rewardLogRepository;

	@Override
	public AdvancedResult<RedeemToken, RedeemTokenTrigger> advance(
			RedeemToken redeemToken, RedeemTokenInstruction instruction, Object message) {

		RetryableCommand collectCommand = executeRedeem(redeemToken);

		if (RetryableCommandHelper.isSuccess(collectCommand)) {
			return new AdvancedResult<>(new TriggerBehavior<>(RedeemTokenTrigger.SUCCESS, this::saveRewardLog));
		}
		return null;
	}

	private RetryableCommand executeRedeem(RedeemToken redeemToken) {
		// 将数字资产转移到归集账户
		CommandIndex commandIndex = new CommandIndex(RedeemToken.TYPE_REF, redeemToken.getId(), "REDEEM");
		return retryableCommandTemplate.execute(commandIndex,
			ci -> Validate.notNull(redeemTokenRepository.lockById(ci.getParentId())), null,
			command -> {
				if (RetryableCommandHelper.isCreated(command)) {
					TransferOrder transferOrder = cahFunction.transfer(
							String.valueOf(command.getId()),
							redeemToken.getAmount().toBigInteger(),
							redeemToken.getScenario().getActivity().getSupplierAddress(),
							redeemToken.getReceiverAddress(), redeemToken.getScenario().getActivity().getAssetCode());
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

	private void saveRewardLog(RedeemToken redeemToken) {
		RewardLog log = new RewardLog();
		log.setAmount(redeemToken.getAmount());
		log.setScenarioId(redeemToken.getScenario().getId());
		log.setActivityId(redeemToken.getScenario().getActivity().getId());
		log.setReceiverAddress(redeemToken.getReceiverAddress());
		rewardLogRepository.save(log);
	}


}