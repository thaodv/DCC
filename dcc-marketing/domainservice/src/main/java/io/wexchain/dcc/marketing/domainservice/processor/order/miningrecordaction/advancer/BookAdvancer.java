package io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.cryptoasset.account.api.constant.ExecutionResult;
import io.wexchain.cryptoasset.account.api.model.AccountTransaction;
import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domain.RetryableCommand;
import io.wexchain.dcc.marketing.domainservice.function.booking.BookingService;
import io.wexchain.dcc.marketing.domainservice.function.command.CommandIndex;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandHelper;
import io.wexchain.dcc.marketing.domainservice.function.command.RetryableCommandTemplate;
import io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.MiningRewardRecordTrigger;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BookAdvancer extends AbstractAdvancer<MiningRewardRecord, MiningRewardRecordInstruction, MiningRewardRecordTrigger> {

	{
		availableStatus = MiningActionRecordStatus.ACCEPTED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MiningRewardRecordRepository miningRewardRecordRepository;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private RetryableCommandTemplate retryableCommandTemplate;

	@Override
	public AdvancedResult<MiningRewardRecord, MiningRewardRecordTrigger> advance(
            MiningRewardRecord miningRewardRecord, MiningRewardRecordInstruction instruction, Object message) {
		bookingService.openAccount(miningRewardRecord.getAddress());  //开户
		RetryableCommand bookCommand = executeBook(miningRewardRecord);

		if (RetryableCommandHelper.isSuccess(bookCommand)) {
			return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRecordTrigger.BOOK));
		}
		return null;
	}

	private RetryableCommand executeBook(MiningRewardRecord miningRewardRecord) {
		// 将数字资产转移到归集账户
		CommandIndex commandIndex = new CommandIndex(MiningRewardRecord.TYPE_REF, miningRewardRecord.getId(), "BOOK");
		return retryableCommandTemplate.execute(commandIndex,
				ci -> Validate.notNull(miningRewardRecordRepository.lockById(ci.getParentId())), null,
				command -> {
					if (RetryableCommandHelper.isCreated(command)) {
						AccountTransaction accounting = bookingService.accounting(miningRewardRecord.getAddress(), String.valueOf(command.getId()), miningRewardRecord.getScore());  //登账
						if (accounting.getResult() == ExecutionResult.SUCCESS) {
							command.setMemo(miningRewardRecord.getScore() + "");
							return GeneralCommandStatus.SUCCESS.name();
						} else {
							return GeneralCommandStatus.FAILURE.name();
						}
					}
					return command.getStatus();
				});
	}


}