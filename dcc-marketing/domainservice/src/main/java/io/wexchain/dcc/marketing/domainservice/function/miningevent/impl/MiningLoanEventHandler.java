package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.notify.domain.dcc.OrderUpdatedEvent;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * MiningEventHandlerImpl
 *
 * @author fu qiliang
 */
public class MiningLoanEventHandler implements MiningEventHandler {

    private String eventName;  //事件名
    private String status;  //事件状态

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private MiningRewardRecordService miningRewardRecordService;

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof OrderUpdatedEvent && status.equalsIgnoreCase(((OrderUpdatedEvent) obj).getCurrentStatus());
    }

    @Override
    public MiningRewardRecord handle(Object obj) {
        if (obj instanceof OrderUpdatedEvent) {
            List<EcoRewardRule> ecoRewardRules = ecoRewardRuleService.queryEcoRewardRuleByEventName(eventName);
            EcoRewardRule ecoRewardRule = ecoRewardRules.get(0);

            MiningRewardRecord rewardRecord = new MiningRewardRecord();
            OrderUpdatedEvent event = (OrderUpdatedEvent) obj;
            rewardRecord.setScore(ecoRewardRule.getScore());
            rewardRecord.setAddress(event.getAddress());
            rewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
            miningRewardRecordService.saveMiningRewardFromEvent(rewardRecord);

            return rewardRecord;
        }
        return null;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
