package io.wexchain.dcc.marketing.domainservice.function.ecoevent.impl;

import io.wexchain.dcc.marketing.api.constant.ParticipatorRole;
import io.wexchain.dcc.marketing.domain.ContractBeneficiary;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.ecoevent.AbstractEcoEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.web3.Web3Function;
import io.wexchain.dcc.marketing.repository.ContractBeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
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
public class LoanEcoEventHandler extends AbstractEcoEventHandler {

    private static Map<String, String> statusMap = new HashMap<>();

    static {
        statusMap.put("INVALID", "0x0000000000000000000000000000000000000000000000000000000000000000");
        statusMap.put("CREATED", "0x0000000000000000000000000000000000000000000000000000000000000001");
        statusMap.put("CANCELLED", "0x0000000000000000000000000000000000000000000000000000000000000002");
        statusMap.put("AUDITING", "0x0000000000000000000000000000000000000000000000000000000000000003");
        statusMap.put("REJECTED", "0x0000000000000000000000000000000000000000000000000000000000000004");
        statusMap.put("APPROVED", "0x0000000000000000000000000000000000000000000000000000000000000005");
        statusMap.put("FAILURE", "0x0000000000000000000000000000000000000000000000000000000000000006");
        statusMap.put("DELIVERIED", "0x0000000000000000000000000000000000000000000000000000000000000007");
        statusMap.put("RECEIVIED", "0x0000000000000000000000000000000000000000000000000000000000000008");
        statusMap.put("REPAID", "0x0000000000000000000000000000000000000000000000000000000000000009");
    }


    private String allowStatus;

    private String eventName;

    @Autowired
    private Web3Function web3Function;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private ContractBeneficiaryRepository contractBeneficiaryRepository;

    @Override
    public List<RewardActionRecord> handle(Log log) {
        if (log.getData().equalsIgnoreCase(statusMap.get(allowStatus))) {
            List<RewardActionRecord> recordList = new ArrayList<>();
            List<EcoRewardRule> ruleList = ecoRewardRuleService.queryEcoRewardRuleByEventName(getEventName());
            for (EcoRewardRule rule : ruleList) {
                RewardActionRecord record = new RewardActionRecord();
                if (rule.getParticipatorRole() == ParticipatorRole.CONTRACT) {
                    String contractOwner = web3Function.getContractOwner(log.getAddress());
                    Assert.notNull(contractOwner, "Not found contract owner " + log.getAddress());
                    record.setAddress(contractOwner);
                }
                if (rule.getParticipatorRole() == ParticipatorRole.USER) {
                    record.setAddress(log.getTopics().get(2).replace("0x000000000000000000000000", "0x"));
                }
                record.setScore(rule.getScore());
                record.setEcoRewardRule(rule);
                record.setTransactionHash(log.getTransactionHash());
                record.setLogIndex(Integer.valueOf(log.getLogIndexRaw()));
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
