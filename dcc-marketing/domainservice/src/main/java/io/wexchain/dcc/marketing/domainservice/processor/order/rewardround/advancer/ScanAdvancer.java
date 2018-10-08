package io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.advancer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.protocol.core.methods.response.Log;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.dcc.marketing.api.constant.ParticipatorRole;
import io.wexchain.dcc.marketing.api.constant.RestrictionType;
import io.wexchain.dcc.marketing.api.constant.RewardActionRecordStatus;
import io.wexchain.dcc.marketing.api.constant.RewardRoundStatus;
import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.function.ecoevent.EcoEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.web3.Web3Function;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundTrigger;
import io.wexchain.dcc.marketing.repository.RewardActionRecordRepository;


public class ScanAdvancer extends AbstractAdvancer<RewardRound, RewardRoundInstruction, RewardRoundTrigger> {

	{
		availableStatus = RewardRoundStatus.CREATED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private Web3Function web3Function;

	private List<EcoEventHandler> eventHandlerList;

	@Autowired
	private RewardActionRecordRepository rewardActionRecordRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	private static final Integer BLOCK_SACN_PAGE_SIZE = 500;

	@Override
	public AdvancedResult<RewardRound, RewardRoundTrigger> advance(
			RewardRound rewardRound, RewardRoundInstruction instruction, Object message) {

		beforeScan(rewardRound);

		List<Pair<Long, Long>> blockScanList = calcBlockScanList(
				rewardRound.getStartBlock(), rewardRound.getEndBlock());

		for (Pair<Long, Long> scanRange : blockScanList) {
			logger.info("Start scan block, from {}, to {}.", scanRange.getLeft(), scanRange.getRight());
			List<Log> eventLogList = web3Function.getEventLogList(
					scanRange.getLeft(), scanRange.getRight());

			List<RewardActionRecord> actionRecordList = new ArrayList<>();
			eventLogList.forEach(log -> {
				eventHandlerList.forEach(handler -> {
					if (handler.canHandle(log)) {
						List<RewardActionRecord> recordList = handler.handle(log);
						if (CollectionUtils.isNotEmpty(recordList)) {
							actionRecordList.addAll(recordList);
						}
					}
				});
			});

			logger.info("RewardActionRecord size is {}", actionRecordList.size());

			checkIdRestriction(actionRecordList, rewardRound);

			rewardActionRecordRepository.saveAll(actionRecordList);
		}

		return new AdvancedResult<>(new TriggerBehavior<>(RewardRoundTrigger.SCAN));
	}

	private void beforeScan(RewardRound rewardRound) {
		Long count = rewardActionRecordRepository.countByRewardRoundId(rewardRound.getId());
		logger.info("Before scan block, reward round id:{}, action record count:{}", rewardRound.getId(), count);
		if (count != null && count > 0) {
			Long deleted = transactionTemplate.execute(
					status -> rewardActionRecordRepository.deleteByRewardRoundId(rewardRound.getId()));
			logger.info("Before scan block, reward round id:{}, delete record count:{}", rewardRound.getId(), deleted);
		}
	}

	private List<Pair<Long, Long>> calcBlockScanList(long startBlock, long endBlock) {
		long totalScanNum = endBlock - startBlock;
		long pageNum = (totalScanNum + BLOCK_SACN_PAGE_SIZE - 1) / BLOCK_SACN_PAGE_SIZE;
		List<Pair<Long, Long>> list = new ArrayList<>();
		for (long page = 1; page <= pageNum; page++) {
			long start, end;
			if (page == 1L) {
				start = startBlock;
			} else {
				start = startBlock + BLOCK_SACN_PAGE_SIZE * (page - 1);
			}
			if (page == pageNum) {
				end = endBlock;
			} else {
				end = startBlock + BLOCK_SACN_PAGE_SIZE * page;
			}
			list.add(Pair.of(start, end));
		}
		logger.info("Block scan list:{}", list);
		return list;
	}

	private void checkIdRestriction(List<RewardActionRecord> actionRecordList, RewardRound rewardRound) {

		actionRecordList.sort((a, b) -> {
			ParticipatorRole roleA = a.getEcoRewardRule().getParticipatorRole();
			ParticipatorRole roleB = b.getEcoRewardRule().getParticipatorRole();
			if (roleA == roleB) {
				return 0;
			}
			if (roleA == ParticipatorRole.USER && roleB == ParticipatorRole.CONTRACT) {
				return -1;
			}
			return 1;
		});

		Set<String> rejectTmp = new HashSet<>();
		Set<String> distinctTmp = new HashSet<>();
		for (RewardActionRecord record : actionRecordList) {

			record.setStatus(RewardActionRecordStatus.ACCEPTED);
			record.setRewardRound(rewardRound);

			if (record.getEcoRewardRule().getScenario().getRestrictionType() == RestrictionType.ID) {
				if (record.getEcoRewardRule().getParticipatorRole() == ParticipatorRole.USER) {
					Optional<String> idHashOpt = chainOrderService.getIdHash(record.getAddress());
					if (!idHashOpt.isPresent()) {
						record.setStatus(RewardActionRecordStatus.REJECTED);
						rejectTmp.add(record.getTransactionHash() + "_" + record.getLogIndex());
						logger.info("Address {} not found id hash, tx hash:{}, event name:{}",
								record.getAddress(), record.getTransactionHash(), record.getEcoRewardRule().getEventName());
						continue;
					}
					String idHash = idHashOpt.get();
					//.orElseThrow(() -> new ContextedRuntimeException("Id hash no found, address:" + record.getAddress()));
					record.setIdHash(idHash);
					// 数据库约束
					if (rewardActionRecordRepository.countByEcoRewardRuleIdAndIdHash(
							record.getEcoRewardRule().getId(), idHash) > 0) {
						record.setStatus(RewardActionRecordStatus.REJECTED);
						rejectTmp.add(record.getTransactionHash() + "_" + record.getLogIndex());
					}
					// 集合约束
					if (!distinctTmp.add(record.getEcoRewardRule().getId() + "_" + idHash)) {
						record.setStatus(RewardActionRecordStatus.REJECTED);
						rejectTmp.add(record.getTransactionHash() + "_" + record.getLogIndex());
					}
				}
				if (record.getEcoRewardRule().getParticipatorRole() == ParticipatorRole.CONTRACT) {
					if (rejectTmp.contains(record.getTransactionHash() + "_" + record.getLogIndex())) {
						record.setStatus(RewardActionRecordStatus.REJECTED);
					}
				}
			}
		}
	}

	public void setEventHandlerList(List<EcoEventHandler> eventHandlerList) {
		this.eventHandlerList = eventHandlerList;
	}
}