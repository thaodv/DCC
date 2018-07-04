package io.wexchain.dcc.marketing.domainservice.function.command;

import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.domain.RetryableCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class RetryableCommandTemplate {
	{
		cmdSuccessStatus = GeneralCommandStatus.SUCCESS.name();
		cmdFailureStatus = GeneralCommandStatus.FAILURE.name();
	}
	@Autowired
	private RetryableCommandFunction retryableCommandFunction;

	private String cmdSuccessStatus;

	private String cmdFailureStatus;

	public void setCmdSuccessStatus(String cmdSuccessStatus) {
		this.cmdSuccessStatus = cmdSuccessStatus;
	}

	public void setCmdFailureStatus(String cmdFailureStatus) {
		this.cmdFailureStatus = cmdFailureStatus;
	}
	
	public RetryableCommand execute(CommandIndex commandIndex, Consumer<CommandIndex> lockCallback,
									Supplier<Map<String, String>> params, Function<RetryableCommand, String> function) {

		RetryableCommand retryableCommand = retryableCommandFunction.lockAndPrepareCommand(commandIndex, lockCallback,
				params, cmdFailureStatus);

		if (cmdSuccessStatus.equals(retryableCommand.getStatus())) {
			return retryableCommand;
		}
		String cmdStatus = function.apply(retryableCommand);
		if (StringUtils.isNotBlank(cmdStatus) && !cmdStatus.equals(retryableCommand.getStatus())) {
			retryableCommand = retryableCommandFunction.updateStatus(retryableCommand, cmdStatus);
		}
		return retryableCommand;
	}
}