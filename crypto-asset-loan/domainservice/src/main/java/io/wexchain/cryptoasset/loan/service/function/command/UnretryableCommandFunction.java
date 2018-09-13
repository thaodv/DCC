package io.wexchain.cryptoasset.loan.service.function.command;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.UnretryableCommand;
import io.wexchain.cryptoasset.loan.repository.UnretryableCommandRepository;
import jodd.bean.BeanCopy;

@Service
public class UnretryableCommandFunction implements ContentEnabled<UnretryableCommand> {

	@Autowired
	private UnretryableCommandRepository unretryableCommandRepository;

	public Long prepareCommandId(CommandIndex index, Map<String, String> params) {
		UnretryableCommand orderCommand = prepareCommand(index, params);
		if (orderCommand != null) {
			return orderCommand.getId();
		} else {
			return null;
		}
	}

	public UnretryableCommand getCommand(CommandIndex index) {
		return unretryableCommandRepository.findByParentTypeAndParentIdAndCommand(index.getParentType(),
				index.getParentId(), index.getCommand());
	}

	public UnretryableCommand prepareCommand(CommandIndex index, String memo, Map<String, String> params) {
		UnretryableCommand unretryableCommand = getCommand(index);
		if (unretryableCommand != null) {
			return unretryableCommand;
		} else {
			UnretryableCommand newOc = new UnretryableCommand();
			BeanCopy.beans(index, newOc).copy();
			newOc.setStatus(GeneralCommandStatus.CREATED.name());
			if (StringUtils.isNotEmpty(memo)) {
				newOc.setMemo(memo);
			}
			if (MapUtils.isNotEmpty(params)) {
				newOc.setParams(params);
			}
			try {
				UnretryableCommand savedCommand = unretryableCommandRepository.save(newOc);
				return savedCommand;
			} catch (DataIntegrityViolationException e) {
				UnretryableCommand oldCommand = unretryableCommandRepository.findByParentTypeAndParentIdAndCommand(
						index.getParentType(), index.getParentId(), index.getCommand());
				if (oldCommand != null) {
					return oldCommand;
				} else {
					throw e;
				}
			}
		}

	}

	public UnretryableCommand prepareCommand(CommandIndex index, Map<String, String> params) {
		UnretryableCommand unretryableCommand = getCommand(index);
		if (unretryableCommand != null) {
			return unretryableCommand;
		} else {
			UnretryableCommand newOc = new UnretryableCommand();
			BeanCopy.beans(index, newOc).copy();
			newOc.setStatus(GeneralCommandStatus.CREATED.name());
			if (MapUtils.isNotEmpty(params)) {
				newOc.setParams(params);
			}
			try {
				UnretryableCommand savedCommand = unretryableCommandRepository.save(newOc);
				return savedCommand;
			} catch (DataIntegrityViolationException e) {
				UnretryableCommand oldCommand = unretryableCommandRepository.findByParentTypeAndParentIdAndCommand(
						index.getParentType(), index.getParentId(), index.getCommand());
				if (oldCommand != null) {
					return oldCommand;
				} else {
					throw e;
				}
			}
		}

	}

	public UnretryableCommand updateStatus(UnretryableCommand command, String status) {
		command.setStatus(status);
		return unretryableCommandRepository.save(command);
	}

	public UnretryableCommand updateStatusById(String commandId, String status) {
		UnretryableCommand command = unretryableCommandRepository.findById(commandId).orElse(null);
		command.setStatus(status);
		return unretryableCommandRepository.save(command);
	}

	public void updateExternalOrderId(String commandId, String externalOrderId) {
		UnretryableCommand orderCommand = unretryableCommandRepository.findById(commandId).orElse(null);
		Validate.notNull(orderCommand, "orderCommand is null");
		if (orderCommand.getExternalOrderId() == null) {
			orderCommand.setExternalOrderId(externalOrderId);
			unretryableCommandRepository.save(orderCommand);
		} else {
			Validate.isTrue(orderCommand.getExternalOrderId().equals(externalOrderId));
		}
	}

	public UnretryableCommand updateMemo(UnretryableCommand command, String memo) {
		command.setMemo(memo);
		return unretryableCommandRepository.save(command);
	}

	public void updateMemo(String commandId, String memo) {
		UnretryableCommand orderCommand = unretryableCommandRepository.findById(commandId).orElse(null);
		Validate.notNull(orderCommand, "orderCommand is null");
		orderCommand.setMemo(memo);
		unretryableCommandRepository.save(orderCommand);
	}

	public UnretryableCommand updateStatusAndMemo(UnretryableCommand command, String status, String memo) {
		command.setStatus(status);
		command.setMemo(memo);
		return unretryableCommandRepository.save(command);
	}

	@Override
	public <CNT> CommandAndContent<UnretryableCommand, CNT> fillContent(UnretryableCommand command,
			Supplier<CNT> consumer) {
		return null;
	}
}
