package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.RewardLogStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.RewardStatistics;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.marketing.domain.RewardLog;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface RewardLogService {

    Page<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request);

    BigDecimal getTotalRewardAmount(GetTotalRewardAmountRequest request);

    RewardLogStatisticsInfo getRewardLogStatisticsInfo(String activityCode);

    BigDecimal getRewardLogAmount(String activityCode);

    List<RewardStatistics> queryTop20RewardLogAmount(String activityCode);
}
