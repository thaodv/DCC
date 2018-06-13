package io.wexchain.dcc.marketing.domainservice.impl;

import io.wexchain.dcc.marketing.api.model.QueryEcoRewardRuleRequest;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.repository.EcoRewardRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EcoRewardRuleServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class EcoRewardRuleServiceImpl implements EcoRewardRuleService {

    private List<EcoRewardRule> ruleList = null;

    @Autowired
    private EcoRewardRuleRepository rewardRuleRepository;

    @Override
    public List<EcoRewardRule> queryEcoRewardRule() {
        return (List<EcoRewardRule>) rewardRuleRepository.findAll();
    }

    @Override
    public List<EcoRewardRule> queryEcoRewardRule(QueryEcoRewardRuleRequest request) {
        if (request.getParticipatorRole() != null) {
            return rewardRuleRepository.findByParticipatorRoleOrderByIdAsc(request.getParticipatorRole());
        }
        return queryEcoRewardRule();
    }

    @Override
    public List<EcoRewardRule> queryEcoRewardRuleByEventName(String eventName) {
        if (ruleList == null) {
            ruleList = queryEcoRewardRule();
        }
        return ruleList.stream().filter(
                rule -> rule.getEventName().equalsIgnoreCase(eventName))
                .collect(Collectors.toList());
    }
}