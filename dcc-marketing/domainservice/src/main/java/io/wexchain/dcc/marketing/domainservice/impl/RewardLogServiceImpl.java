package io.wexchain.dcc.marketing.domainservice.impl;

import com.wexmarket.topia.commons.data.page.PageUtils;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.marketing.domain.RewardLog;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.RewardLogService;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.repository.RewardLogRepository;
import io.wexchain.dcc.marketing.repository.query.RewardLogQueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Override
    public Page<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request) {
        Long activityId = null;
        Long scenarioId = null;
        if (StringUtils.isNotEmpty(request.getActivityCode())) {
            activityId = activityService.getActivityByCode(request.getActivityCode()).getId();
        }
        if (StringUtils.isNotEmpty(request.getScenarioCode())) {
            scenarioId = scenarioService.getScenario(request.getActivityCode()).getId();
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
}
