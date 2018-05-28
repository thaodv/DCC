package io.wexchain.cryptoasset.loan.service.function.command;

import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.RetryableCommand;
import io.wexchain.cryptoasset.loan.repository.RetryableCommandRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class RetryableCommandFunction {

	@Autowired
	private RetryableCommandRepository retryableCommandRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	public RetryableCommand getNotFailureCommand(CommandIndex commandIndex, String failureStatus) {
		return retryableCommandRepository.findByParentTypeAndParentIdAndCommandAndStatusNot(
				commandIndex.getParentType(), commandIndex.getParentId(), commandIndex.getCommand(), failureStatus);
	}

	public RetryableCommand lockAndPrepareCommand(CommandIndex commandIndex, Consumer<CommandIndex> lockCallback,
			Supplier<Map<String, String>> paramsSupplier, String failureStatus) {
		RetryableCommand retryableCommand = getNotFailureCommand(commandIndex, failureStatus);
		if (retryableCommand != null) {
			return retryableCommand;
		}
		Map<String, String> params = paramsSupplier != null ? paramsSupplier.get() : null;
		retryableCommand = transactionTemplate.execute(arg0 -> {
			lockCallback.accept(commandIndex);
			return prepareCommand(commandIndex, params, failureStatus);
		});
		return Validate.notNull(retryableCommand, "retryableCommand is null");
	}

	/**
	 * 在事务内使用需要锁业务对象
	 */
	public RetryableCommand prepareCommand(CommandIndex commandIndex, Map<String, String> params,
			String failureStatus) {
		RetryableCommand generalCommand = getNotFailureCommand(commandIndex, failureStatus);
		if (generalCommand != null) {
			return generalCommand;
		} else {
			RetryableCommand newGc = new RetryableCommand();
			newGc.setParentType(commandIndex.getParentType());
			newGc.setParentId(commandIndex.getParentId());
			newGc.setCommand(commandIndex.getCommand());
			newGc.setParams(params);
			newGc.setStatus(GeneralCommandStatus.CREATED.name());
			return retryableCommandRepository.save(newGc);
		}
	}

	public RetryableCommand updateStatus(String commandId, String status) {
		return transactionTemplate.execute(status1 -> {
			RetryableCommand retryableCommand = retryableCommandRepository.findById(commandId).orElse(null);
			Validate.notNull(retryableCommand, "retryableCommand is null");
			retryableCommand.setStatus(status);
			return retryableCommandRepository.save(retryableCommand);
		});

	}

	public RetryableCommand updateStatus(RetryableCommand command, String status) {
		command.setStatus(status);
		return retryableCommandRepository.save(command);
	}
}
