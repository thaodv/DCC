package io.wexchain.dcc.service.frontend.integration.marketing;

import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.QueryEcoRewardRuleRequest;
import io.wexchain.dcc.marketing.api.model.RewardRound;
import io.wexchain.dcc.marketing.api.model.request.CreateRewardRoundRequest;

import java.util.List;

public interface EcoRewardOperationClient {

    /**
     * 查询奖励规则
     */
    List<EcoRewardRule> queryEcoRewardRule(QueryEcoRewardRuleRequest request);

    /**
     * 查询生态奖励统计
     */
    EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address);

    /**
     * 创建奖励轮次
     */
    RewardRound createRewardRound(CreateRewardRoundRequest request);
}
