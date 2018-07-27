package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.data.page.PageUtils;
import io.wexchain.cryptoasset.account.api.constant.ExecutionResult;
import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.cryptoasset.account.api.model.AccountTransaction;
import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.facade.AddMiningScoreRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.marketing.common.constant.GeneralCommandStatus;
import io.wexchain.dcc.marketing.common.constant.IdentityType;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.*;
import io.wexchain.dcc.marketing.domainservice.*;
import io.wexchain.dcc.marketing.domainservice.function.booking.BookingService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrecord.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.CoolDownRestrictionRepository;
import io.wexchain.dcc.marketing.repository.EcoRewardRuleRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MiningRewardRecordServiceImpl
 *
 * @author fu qiliang
 */
@Service("miningRewardRecordService")
public class MiningRewardRecordServiceImpl implements MiningRewardRecordService, Patroller {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "miningRewardRecordExecutor")
	private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

	@Autowired
	private MiningRewardRecordRepository miningRewardRecordRepository;

	@Autowired
	private EcoRewardRuleRepository ecoRewardRuleRepository;

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private ScenarioService scenarioService;

	@Autowired
	private CoolDownConfigService coolDownConfigService;

	@Autowired
	private CoolDownRestrictionRepository coolDownRestrictionRepository;

	@Autowired
	private EcoRewardRuleService ecoRewardRuleService;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private BookingService bookingService;

	private static final String SIGN_IN_SCENARIO_CODE = "10004005";
	private static final String SIGN_IN_CD_CONFIG_CODE = "10001";
	private static final String SIGN_IN_EVENT_NAME = "MINING_SIGN_IN";

	@Override
	public void saveMiningRewardFromEvent(MiningRewardRecord rewardRecord) {
		MiningRewardRecord mining = miningRewardRecordRepository.save(rewardRecord);
		miningRwdRecExecutor.executeAsync(mining, null, null);
	}

	@Override
	public Page<MiningRewardRecord> queryMiningRewardRecordPage(QueryMiningRewardRecordPageRequest request) {
		PageRequest pageRequest = PageUtils.convert(request.getSortPageParam());
		return miningRewardRecordRepository.findByAddress(request.getAddress(), pageRequest);
	}

	@Override
    public List<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request) {
		List<EcoRewardRule> list = ecoRewardRuleRepository.findByScenarioActivityCodeAndParticipatorRoleOrderByIdAsc(
				request.getActivityCode(), request.getParticipatorRole());
		return list.stream().filter(rule -> StringUtils.isNotEmpty(rule.getGroupCode())).collect(Collectors.toList());
    }

	@Override
	public BigDecimal getYesterdayMiningScore() {
		Date from = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
		Date to = DateTime.now().withTimeAtStartOfDay().minusMillis(1).toDate();
		return miningRewardRecordRepository.sumScore(from,to);
	}

    @Override
    public void signIn(String address) {
		Date activeTime = new Date();
		// TODO 修改错误代码
		String idHash = chainOrderService.getIdHash(address).orElseThrow(() ->
				new ErrorCodeException(MarketingErrorCode.ACTIVITY_IS_ENDED.name(), "用户未实名"));
		Scenario scenario = scenarioService.getScenarioByCode(SIGN_IN_SCENARIO_CODE);
		CoolDownConfig cdConfig = coolDownConfigService.getCoolDownConfigByCode(SIGN_IN_CD_CONFIG_CODE);
		EcoRewardRule rule = ecoRewardRuleService.queryEcoRewardRuleByEventName(SIGN_IN_EVENT_NAME).get(0);

		CoolDownRestriction restriction = coolDownRestrictionRepository
				.findByScenarioIdAndIdentity(scenario.getId(), idHash);

		if (restriction != null) {
			if (allowActive(cdConfig, activeTime, restriction.getLastActiveTime())) {
				restriction.setLastActiveTime(activeTime);
			} else {
				throw new ErrorCodeException(MarketingErrorCode.ACTIVITY_IS_ENDED.name(), "重复签到");
			}
		} else {
			restriction = new CoolDownRestriction();
			restriction.setScenario(scenario);
			restriction.setCoolDownConfig(cdConfig);
			restriction.setIdentity(idHash);
			restriction.setIdentityType(IdentityType.ID_HASH);
			restriction.setLastActiveTime(activeTime);
		}

		CoolDownRestriction restrictionForSave = restriction;
		MiningRewardRecord rewardRecord = transactionTemplate.execute(status -> {
			coolDownRestrictionRepository.save(restrictionForSave);

			MiningRewardRecord newRewardRecord = new MiningRewardRecord();
			newRewardRecord.setScore(rule.getScore());
			newRewardRecord.setAddress(address);
			newRewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
			newRewardRecord.setRewardRule(rule);
			return miningRewardRecordRepository.save(newRewardRecord);
		});

		miningRwdRecExecutor.executeAsync(rewardRecord, null, null);
	}

	@Override
	public Integer yesterdaySignInCount() {
		Scenario scenario = scenarioService.getScenarioByCode(SIGN_IN_SCENARIO_CODE);
		Date from = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
		Date to = DateTime.now().withTimeAtStartOfDay().minusMillis(1).toDate();
		int count = coolDownRestrictionRepository.countByScenarioIdAndLastActiveTimeBetween(scenario.getId(), from, to);
		return count;
	}

	@Override
	public BigDecimal getMiningScore(String address) {
		Account account = bookingService.getAccountByCode(address);
		return account.getBalance();
	}

	@Override
	public BigDecimal addMiningScore(AddMiningScoreRequest request) {
		AccountTransaction accountTransaction;
		if (request.getScore().compareTo(BigDecimal.ZERO) >= 0) {
			accountTransaction = bookingService.add(
					request.getAddress(), UUID.randomUUID().toString().replace("-", ""), request.getScore());
		} else {
			accountTransaction = bookingService.subtract(
					request.getAddress(), UUID.randomUUID().toString().replace("-", ""), request.getScore().abs());
		}
		if (accountTransaction.getResult() == ExecutionResult.SUCCESS) {
			return getMiningScore(request.getAddress());
		} else {
			throw new ErrorCodeException("ADD_MINING_SCORE_FAIL", accountTransaction.getErrorCode().name());
		}
	}

	private boolean allowActive(CoolDownConfig config, Date activeTime, Date lastActiveTime) {
		if (config.getTruncate()) {
			switch (config.getTimeUnit()) {
				case DAYS: {
					DateTime d1 = new DateTime(activeTime).withTimeAtStartOfDay();
					DateTime d2 = new DateTime(lastActiveTime).withTimeAtStartOfDay();
					return d1.minusDays(config.getPeriod()).getMillis() >= d2.getMillis();
				}
				case MINUTES: {
					if (activeTime.getTime() - lastActiveTime.getTime() > config.getPeriod() * 60 * 1000) {
						return true;
					}
					int m1 = new DateTime(activeTime).getMinuteOfDay();
					int m2 = new DateTime(lastActiveTime).getMinuteOfDay();
					return m1 - m2 >= config.getPeriod();
				}
			}
		}
		return false;
	}

	@Override
	public void patrol() {
		DateTime now = new DateTime();
		Date beginTime = now.minusDays(60).toDate();
		Date endTime = now.minusMinutes(1).toDate();

		List<MiningRewardRecord> list = miningRewardRecordRepository
				.findTop1000ByStatusInAndCreatedTimeBetweenOrderByIdAsc(
				Collections.singletonList(MiningActionRecordStatus.ACCEPTED), beginTime, endTime);
		logger.info("Patrol mining reward record size: {}", list.size());

		for (MiningRewardRecord miningRewardRecord : list) {
			miningRwdRecExecutor.executeAsync(miningRewardRecord, null, null);
		}
	}


}
