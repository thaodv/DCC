package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.CustomeValidate;
import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.constant.RewardRoundStatus;
import io.wexchain.dcc.marketing.api.model.EcoRewardRankVo;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.RewardDelivery;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.web3.Web3Function;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundInstruction;
import io.wexchain.dcc.marketing.repository.RewardActionRecordRepository;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import io.wexchain.dcc.marketing.repository.RewardRoundRepository;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RewardRoundServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RewardRoundServiceImpl implements RewardRoundService {

    @Autowired
    private CahFunction cahFunction;

    @Autowired
    private Web3Function web3Function;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RewardRoundRepository rewardRoundRepository;

    @Resource(name = "rewardRoundExecutor")
    private OrderExecutor<RewardRound, RewardRoundInstruction> rewardRoundExecutor;

    private static final String ECO_REWARD_ACTIVITY_CODE = "10003";

    @Autowired
    private RewardActionRecordRepository rewardActionRecordRepository;

    @Autowired
    private RewardDeliveryRepository rewardDeliveryRepository;

    @Override
    public RewardRound createRewardRound(Date bonusDay) {
        Date bd = new DateTime(bonusDay).withTimeAtStartOfDay().toDate();
        RewardRound rewardRound = findRewardRoundByBonusDay(bd).orElseGet(() -> {
            checkEcoRewardPoolBalance();
            RewardRound rr = new RewardRound();
            rr.setStartBlock(getStartBlockNumber(bd));
            rr.setEndBlock(getEndBlockNumber(bd));
            rr.setStatus(RewardRoundStatus.CREATED);
            rr.setActivity(activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE));
            rr.setBonusDay(bd);
            return rewardRoundRepository.save(rr);
        });
        rewardRoundExecutor.executeAsync(rewardRound, null ,null);
        return rewardRound;
    }

    @Override
    public Optional<RewardRound> findRewardRoundByBonusDay(Date bonusDay) {
        return Optional.ofNullable(rewardRoundRepository.findByBonusDay(bonusDay));
    }

    @Override
    public EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address) {
        Date yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
        EcoRewardStatisticsInfo info = new EcoRewardStatisticsInfo();
        info.setYesterdayAmount(BigDecimal.ZERO);
        info.setYesterdayScore(BigDecimal.ZERO);
        findRewardRoundByBonusDay(yesterday).ifPresent(rewardRound -> {
            CustomeValidate.isTrue(rewardRound.getStatus() == RewardRoundStatus.DELIVERED,
                    MarketingErrorCode.INVALID_STATUS.name(), "奖励发放中，请稍候重试");
            BigDecimal score = Optional.ofNullable(rewardActionRecordRepository.sumScoreByAddress(rewardRound.getId(), address))
                    .orElse(BigDecimal.ZERO);
            BigDecimal amount = Optional.ofNullable(rewardDeliveryRepository.sumAmountByAddress(rewardRound.getId(), address))
                    .orElse(BigDecimal.ZERO);
            info.setYesterdayScore(score);
            info.setYesterdayAmount(amount);
        });
        return info;
    }

    @Override
    public BigDecimal getEcoScore() {
        Date yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
        Optional<RewardRound> rewardRoundByBonusDay = findRewardRoundByBonusDay(yesterday);
        if(rewardRoundByBonusDay.isPresent()){
            return Optional.ofNullable(rewardActionRecordRepository.sumScore(rewardRoundByBonusDay.get().getId()))
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<EcoRewardRankVo> queryEcoRewardRankList(DateTime roundTime) {
        DateTime trimRoundTime = roundTime.withTimeAtStartOfDay();
        RewardRound rewardRound = findRewardRoundByBonusDay(trimRoundTime.toDate())
                .orElseThrow(() -> new ContextedRuntimeException("轮次不存在"));
        List<RewardDelivery> rewardDeliveryList =
                rewardDeliveryRepository.findTop10ByRewardRoundIdOrderByAmountDesc(rewardRound.getId());
        List<String> addressList = rewardDeliveryList.stream().map(RewardDelivery::getBeneficiaryAddress)
                .collect(Collectors.toList());
        List<Map<String, Object>> actionList = rewardActionRecordRepository.sumScoreGroupByAddressAndAddressIn(
                rewardRound.getId(), addressList);
        List<EcoRewardRankVo> voList = new ArrayList<>();

        for (RewardDelivery delivery : rewardDeliveryList) {
            EcoRewardRankVo vo = new EcoRewardRankVo();
            vo.setAddress(delivery.getBeneficiaryAddress());
            actionList.forEach(map -> {
                if (map.get("address").toString().equalsIgnoreCase(delivery.getBeneficiaryAddress())) {
                    vo.setScore(new BigDecimal(map.get("totalScore") + ""));
                }
            });
            vo.setAmount(delivery.getAmount());
            voList.add(vo);
        }
        return voList;
    }

    private Long getStartBlockNumber(Date bonusDay) {
        /*Page<RewardRound> lastRewardRound = rewardRoundRepository.findAll(
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "createdTime")));
        if (CollectionUtils.isNotEmpty(lastRewardRound.getContent())) {
            return lastRewardRound.getContent().get(0).getEndBlock();
        }*/
        return web3Function.getBlockNumberAfterTime(bonusDay);
    }

    private Long getEndBlockNumber(Date bonusDay) {
        DateTime day = new DateTime(bonusDay).plusDays(1);
        return web3Function.getBlockNumberAfterTime(day.toDate());
    }

    private void checkEcoRewardPoolBalance() {
        Activity activity = activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE);
        BigInteger juzixDccBalance = cahFunction.getJuzixDccBalance(activity.getSupplierAddress());
    }
}
