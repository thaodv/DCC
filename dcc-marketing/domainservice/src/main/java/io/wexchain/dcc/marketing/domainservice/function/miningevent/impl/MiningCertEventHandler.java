package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import io.wexchain.dcc.marketing.api.constant.RestrictionType;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrecord.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.VerifiedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * MiningEventHandlerImpl
 *
 * @author zhengpeng
 */
public class MiningCertEventHandler implements MiningEventHandler {

    /**
     * 时间名称
     */
    private String eventName;

    /**
     * 响应状态
     */
    private String status;

    /**
     * 认证类型
     */
    private String certType;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MiningRewardRecordRepository miningRewardRecordRepository;

    @Resource(name = "miningRewardRecordExecutor")
    private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof VerifiedEvent && status.equalsIgnoreCase(((VerifiedEvent) obj).getStatus().name())
                && certType.equalsIgnoreCase(((VerifiedEvent) obj).getCertType().name());
    }

    @Override
    public MiningRewardRecord handle(Object obj) {

        if (!(obj instanceof VerifiedEvent)) {
            return null;
        }
        VerifiedEvent event = (VerifiedEvent) obj;
        List<EcoRewardRule> ruleList = ecoRewardRuleService.queryEcoRewardRuleByEventName(getEventName());
        EcoRewardRule rule = ruleList.get(0);

        String idHash = checkIdRestriction(rule, event.getAddress());
        if (idHash == null) {
            return null;
        }

        MiningRewardRecord record = transactionTemplate.execute(status -> {
            MiningRewardRecord rewardRecord = new MiningRewardRecord();
            rewardRecord.setAddress(event.getAddress());
            rewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
            rewardRecord.setScore(rule.getScore());
            rewardRecord.setRewardRule(rule);
            rewardRecord = miningRewardRecordRepository.save(rewardRecord);

            IdRestriction idRestriction = new IdRestriction();
            idRestriction.setScenario(rule.getScenario());
            idRestriction.setIdHash(idHash);
            idRestrictionRepository.save(idRestriction);

            return rewardRecord;
        });

        miningRwdRecExecutor.executeAsync(record, null, null);

        return null;
    }

    protected String checkIdRestriction(EcoRewardRule rule, String address) {
        if (rule.getScenario().getRestrictionType() == RestrictionType.ID) {
            Optional<String> idHashOpt = chainOrderService.getIdHash(address);
            if (idHashOpt.isPresent()) {
                String idHash =  idHashOpt.get();
                IdRestriction idRestriction = idRestrictionRepository.findByScenarioIdAndIdHash(
                        rule.getScenario().getId(), idHashOpt.get());
                if (idRestriction != null) {
                    return null;
                }
                return idHash;
            } else {
                return null;
            }
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

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }
}
