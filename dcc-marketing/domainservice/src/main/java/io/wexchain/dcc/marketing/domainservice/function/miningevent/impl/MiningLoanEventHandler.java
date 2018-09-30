package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.godmonth.status.executor.intf.OrderExecutor;

import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrecord.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.OrderUpdatedEvent;

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
    private MiningRewardRecordRepository miningRewardRecordRepository;

    @Resource(name = "miningRewardRecordExecutor")
    private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

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
            rewardRecord.setRewardRule(ecoRewardRule);

            rewardRecord = miningRewardRecordRepository.save(rewardRecord);
            miningRwdRecExecutor.executeAsync(rewardRecord, null, null);

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
