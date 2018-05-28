package io.wexchain.cryptoasset.loan.service.function.command;

import io.wexchain.cryptoasset.loan.domain.characteristics.Command;

import java.util.function.Supplier;

public interface ContentEnabled<CMD extends Command> {
	<CNT> CommandAndContent<CMD, CNT> fillContent(CMD command, Supplier<CNT> consumer);
}
