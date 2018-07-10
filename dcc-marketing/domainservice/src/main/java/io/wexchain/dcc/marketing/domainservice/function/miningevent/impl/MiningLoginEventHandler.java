package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.competition.LockTemplate2;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrecord.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.LastLoginTimeRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * MiningLoginEventHandler
 *
 * @author zhengpeng
 */
public class MiningLoginEventHandler implements MiningEventHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

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

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    @Qualifier("cronLockTemplate")
    private LockTemplate2 lockTemplate;

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof LoginEvent;
    }

    @Override
    public MiningRewardRecord handle(Object obj) {
        if (!(obj instanceof LoginEvent)) {
            return null;
        }
        return null;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
