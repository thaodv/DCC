package io.wexchain.cryptoasset.loan.repository;


import io.wexchain.cryptoasset.loan.domain.RetryableCommand;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RetryableCommandRepository
		extends PagingAndSortingRepository<RetryableCommand, String>, JpaSpecificationExecutor<RetryableCommand> {

	List<RetryableCommand> findByParentTypeAndParentIdAndCommand(String parentType, Long parentId, String command);

	RetryableCommand findByParentTypeAndParentIdAndCommandAndStatusNot(String parentType, Long parentId, String command,
                                                                       String status);

	List<RetryableCommand> findByParentTypeAndParentIdAndCommandAndStatusOrderByCreatedTimeDesc(
			String parentType, Long parentId, String command, String status);

	List<RetryableCommand> findByParentTypeAndParentIdAndCommandInOrderByCreatedTimeDesc(
			String parentType, Long parentId, List<String> commandList);
}
