package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.LastLoginTime;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.LastLoginTimeRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * MiningLoginEventHandler
 *
 * @author zhengpeng
 */
public class MiningLoginEventHandler implements MiningEventHandler {

    private String eventName;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private MiningRewardRecordService miningRewardRecordService;

    @Resource(name = "miningRewardRecordExecutor")
    private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

    @Autowired
    private MiningRewardRecordRepository miningRewardRecordRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private LastLoginTimeRepository lastLoginTimeRepository;

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof LoginEvent;
    }

    @Override
    public MiningRewardRecord handle(Object obj) {
        if (!(obj instanceof LoginEvent)) {
            return null;
        }
        LoginEvent event = (LoginEvent) obj;

        LastLoginTime lastLoginTime = lastLoginTimeRepository.findByAddress(event.getAddress());
        if (lastLoginTime != null) {
            DateTime loginDateTime = new DateTime(event.getLoginDate()).withTimeAtStartOfDay();
            DateTime lastLoginDateTime = new DateTime(lastLoginTime.getLastLoginTime()).withTimeAtStartOfDay();
            if (loginDateTime.getMillis() <= lastLoginDateTime.getMillis()) {
                return null;
            }
        }

        EcoRewardRule rule = ecoRewardRuleService.queryEcoRewardRuleByEventName(eventName).get(0);
        Validate.notNull(rule, "Login event rule is null, even address:{}", event.getAddress());

        MiningRewardRecord rewardRecord = transactionTemplate.execute(status -> {
            if (lastLoginTime != null) {
                lastLoginTime.setLastLoginTime(event.getLoginDate());
                lastLoginTimeRepository.save(lastLoginTime);
            } else {
                LastLoginTime newOne = new LastLoginTime();
                newOne.setAddress(event.getAddress());
                newOne.setLastLoginTime(event.getLoginDate());
                lastLoginTimeRepository.save(newOne);
            }
            MiningRewardRecord newRewardRecord = new MiningRewardRecord();
            newRewardRecord.setScore(rule.getScore());
            newRewardRecord.setAddress(event.getAddress());
            newRewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
            return miningRewardRecordRepository.save(newRewardRecord);
        });

        miningRwdRecExecutor.executeAsync(rewardRecord, null, null);

        return rewardRecord;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
