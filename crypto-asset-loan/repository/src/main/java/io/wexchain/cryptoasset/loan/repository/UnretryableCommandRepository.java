package io.wexchain.cryptoasset.loan.repository;

import io.wexchain.cryptoasset.loan.domain.UnretryableCommand;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UnretryableCommandRepository
		extends PagingAndSortingRepository<UnretryableCommand, String>, JpaSpecificationExecutor<UnretryableCommand> {
	
	UnretryableCommand findByParentTypeAndParentIdAndCommand(String parentType, Long parentId, String command);


	List<UnretryableCommand> findByParentTypeAndParentIdAndCommandInOrderByCreatedTimeDesc(
			String parentType, Long parentId, List<String> commandList);

}
