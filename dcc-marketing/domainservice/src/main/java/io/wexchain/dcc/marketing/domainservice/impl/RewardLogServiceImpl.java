package io.wexchain.dcc.marketing.domainservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.wexchain.dcc.marketing.api.constant.RewardDeliveryStatus;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.data.page.PageUtils;

import io.wexchain.dcc.marketing.api.model.RewardLogStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.RewardStatistics;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.RewardLog;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.RewardLogService;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.repository.RewardLogRepository;
import io.wexchain.dcc.marketing.repository.query.RewardLogQueryBuilder;

/**
 * RewardLogServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RewardLogServiceImpl implements RewardLogService {

    @Autowired
    private RewardLogRepository rewardLogRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private RewardDeliveryRepository rewardDeliveryRepository;

    @Autowired
    private RewardRoundService rewardRoundService;

    @Override
    public Page<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request) {
        Long activityId = null;
        Long scenarioId = null;
        if (StringUtils.isNotEmpty(request.getActivityCode())) {
            activityId = activityService.getActivityByCode(request.getActivityCode()).getId();
        }
        if (StringUtils.isNotEmpty(request.getScenarioCode())) {
            scenarioId = scenarioService.getScenarioByCode(request.getActivityCode()).getId();
        }

        PageRequest pageRequest = PageUtils.convert(request.getSortPageParam());
        return rewardLogRepository.findAll(
                RewardLogQueryBuilder.query(request.getAddress(), activityId, scenarioId), pageRequest);
    }

    @Override
    public BigDecimal getTotalRewardAmount(GetTotalRewardAmountRequest request) {
        Long activityId = activityService.getActivityByCode(request.getActivityCode()).getId();
        return Optional.ofNullable(rewardLogRepository.findTotalAmount(request.getAddress(), activityId))
                .orElseGet(() -> BigDecimal.ZERO) ;
    }

    @Override
    public RewardLogStatisticsInfo getRewardLogStatisticsInfo(String activityCode) {
        Activity activity = activityService.getActivityByCode(activityCode);
        DateTime from = DateTime.now().minusDays(1).withTimeAtStartOfDay();
        DateTime to = DateTime.now().withTimeAtStartOfDay().minusMillis(1);



        RewardLogStatisticsInfo info = new RewardLogStatisticsInfo();
        info.setActivityCode(activity.getCode());
        info.setTotalAmount(Optional.ofNullable(
                rewardLogRepository.findTotalAmount(activity.getId())).orElse(BigDecimal.ZERO));

        if (activity.getCode().equals("10003")) {
            DateTime bonusDay = DateTime.now().minusDays(2).withTimeAtStartOfDay();
            RewardRound rewardRound = rewardRoundService.findRewardRoundByBonusDay(bonusDay.toDate()).get();
            BigDecimal total = rewardDeliveryRepository.sumAmountByRoundId(rewardRound.getId(), RewardDeliveryStatus.SUCCESS);
            int number = rewardDeliveryRepository.countByRewardRoundIdAndStatus(rewardRound.getId(), RewardDeliveryStatus.SUCCESS);
            info.setYesterdayAmount(Optional.ofNullable(total).orElse(BigDecimal.ZERO));
            info.setYesterdayPersonNumber(number);
        } else {
            info.setYesterdayAmount(Optional.ofNullable(
                    rewardLogRepository.findTotalAmountBetweenDate(
                            activity.getId(), from.toDate(), to.toDate())).orElse(BigDecimal.ZERO));
            info.setYesterdayPersonNumber(rewardLogRepository.countByActivityIdAndCreatedTimeBetween(
                    activity.getId(), from.toDate(), to.toDate()));
        }

        return info;
    }

    @Override
    public BigDecimal getRewardLogAmount(String activityCode) {
        DateTime from = DateTime.now().minusDays(1).withTimeAtStartOfDay();
        DateTime to = DateTime.now().withTimeAtStartOfDay().minusMillis(1);
        Activity activity = activityService.getActivityByCode(activityCode);
        return Optional.ofNullable(rewardLogRepository.findTotalAmountBetweenDate(
                activity.getId(), from.toDate(), to.toDate())).orElse(BigDecimal.ZERO);
    }

    @Override
    public List<RewardStatistics> queryTop20RewardLogAmount(String activityCode) {
        DateTime now = DateTime.now();
        Date from = now.plusDays(-now.dayOfWeek().get() + 1).withTimeAtStartOfDay().minusWeeks(1).toDate();
        Date to = now.plusDays(-now.dayOfWeek().get() + 1).withTimeAtStartOfDay().minusMillis(1).toDate();
        Activity activity = activityService.getActivityByCode(activityCode);
        List<Map<String, Object>> topNTotalAmountBetweenDate = rewardLogRepository.findTopNTotalAmountBetweenDate(20, activity.getId(), from, to);
        List<RewardStatistics> rewardStatisticsList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(topNTotalAmountBetweenDate)){
            topNTotalAmountBetweenDate.forEach(map -> {
                RewardStatistics rewardStatistics = new RewardStatistics();
                rewardStatistics.setAddress(map.get("address").toString());
                rewardStatistics.setSumAmount(new BigDecimal(map.get("totalAmount").toString()));
                rewardStatisticsList.add(rewardStatistics);
            });
        }
        return rewardStatisticsList;
    }
}
