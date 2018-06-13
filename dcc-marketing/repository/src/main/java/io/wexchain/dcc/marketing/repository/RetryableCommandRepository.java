package io.wexchain.dcc.marketing.repository;


import io.wexchain.dcc.marketing.domain.RetryableCommand;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RetryableCommandRepository
		extends PagingAndSortingRepository<RetryableCommand, Long>, JpaSpecificationExecutor<RetryableCommand> {



	List<RetryableCommand> findByParentTypeAndParentIdAndCommand(String parentType, Long parentId, String command);

	RetryableCommand findByParentTypeAndParentIdAndCommandAndStatusNot(String parentType, Long parentId, String command,
																	   String status);

	List<RetryableCommand> findByParentTypeAndParentIdAndCommandAndStatusOrderByCreatedTimeDesc(
			String parentType, Long parentId, String command, String status);

	List<RetryableCommand> findByParentTypeAndParentIdAndCommandInOrderByCreatedTimeDesc(
			String parentType, Long parentId, List<String> commandList);
}
