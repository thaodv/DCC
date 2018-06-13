package io.wexchain.dcc.marketing.domainservice.function.ecoevent.impl;

import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.ecoevent.AbstractEcoEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.core.methods.response.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CertEcoEventHandler
 *
 * @author zhengpeng
 */
public class CertEcoEventHandler extends AbstractEcoEventHandler {

    private static Map<String, String> statusMap = new HashMap<>();

    static {
        statusMap.put("INVALID", "0x0000000000000000000000000000000000000000000000000000000000000000");
        statusMap.put("APPLIED", "0x0000000000000000000000000000000000000000000000000000000000000001");
        statusMap.put("PASSED", "0x0000000000000000000000000000000000000000000000000000000000000002");
        statusMap.put("REJECTED", "0x0000000000000000000000000000000000000000000000000000000000000003");
        statusMap.put("DISCARDED", "0x0000000000000000000000000000000000000000000000000000000000000004");
        statusMap.put("REVOKED", "0x0000000000000000000000000000000000000000000000000000000000000005");
    }

    private String allowStatus;

    private String eventName;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Override
    public List<RewardActionRecord> handle(Log log) {
        if (log.getData().equalsIgnoreCase(statusMap.get(allowStatus))) {
            List<RewardActionRecord> recordList = new ArrayList<>();
            List<EcoRewardRule> ruleList = ecoRewardRuleService.queryEcoRewardRuleByEventName(getEventName());
            for (EcoRewardRule rule : ruleList) {
                RewardActionRecord record = new RewardActionRecord();
                record.setAddress(log.getTopics().get(1).replace("0x000000000000000000000000", "0x"));
                record.setScore(rule.getScore());
                //record.setScenario(rule.getScenario());
                record.setEcoRewardRule(rule);
                recordList.add(record);
            }
            return recordList;
        }
        return null;
    }

    public String getAllowStatus() {
        return allowStatus;
    }

    public void setAllowStatus(String allowStatus) {
        this.allowStatus = allowStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
