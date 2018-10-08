package io.wexchain.dcc.marketing.domainservice.function.ecoevent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.core.methods.response.Log;

import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;

/**
 * AbstractEcoEventHandler
 *
 * @author zhengpeng
 */
public abstract class AbstractEcoEventHandler implements EcoEventHandler {

    private String contractAddress;

    private String methodHash;

    private List<EcoRewardRule> ruleList = null;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Override
    public boolean canHandle(Log log) {
        return log != null &&
                log.getTopics().get(0).equalsIgnoreCase(methodHash) &&
                log.getAddress().equalsIgnoreCase(contractAddress);
    }

    @Override
    public abstract List<RewardActionRecord> handle(Log log);

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }
}
