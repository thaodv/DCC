package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.QueryEcoRewardRuleRequest;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;

import java.util.List;

public interface EcoRewardRuleService {

    List<EcoRewardRule> queryEcoRewardRule();

    List<EcoRewardRule> queryEcoRewardRule(QueryEcoRewardRuleRequest request);

    List<EcoRewardRule> queryEcoRewardRuleByEventName(String eventName);

}
