package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.RewardLogStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.marketing.domain.RewardLog;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface RewardLogService {

    Page<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request);

    BigDecimal getTotalRewardAmount(GetTotalRewardAmountRequest request);

    RewardLogStatisticsInfo getRewardLogStatisticsInfo(String activityCode);
}
