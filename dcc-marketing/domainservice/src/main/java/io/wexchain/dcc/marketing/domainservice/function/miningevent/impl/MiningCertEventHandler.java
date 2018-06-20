package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import afu.org.checkerframework.checker.units.qual.A;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.marketing.api.constant.RestrictionType;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.function.miningevent.MiningEventHandler;
import io.wexchain.dcc.marketing.domainservice.function.wexyun.WexyunLoanClient;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.notify.domain.dcc.CertOrderStatus;
import io.wexchain.notify.domain.dcc.OrderUpdatedEvent;
import io.wexchain.notify.domain.dcc.VerifiedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * MiningEventHandlerImpl
 *
 * @author zhengpeng
 */
public class MiningCertEventHandler implements MiningEventHandler {

    private String eventName;

    private String status;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private WexyunLoanClient wexyunLoanClient;

    private static final String BONUS_CODE_ID_CERT_BASIC = "MINING_ID_CERT_PASS_BASIC";
    private static final String BONUS_CODE_ID_CERT_INVITE = "MINING_ID_CERT_PASS_INVITE";
    private static final String BONUS_CODE_ID_CERT_FRIEND = "MINING_ID_CERT_PASS_FRIEND";

    @Override
    public boolean canHandle(Object obj) {
        return obj instanceof VerifiedEvent && status.equalsIgnoreCase(((VerifiedEvent) obj).getStatus().name());
    }

    @Override
    public MiningRewardRecord handle(Object obj) {

        if (!(obj instanceof VerifiedEvent)) {
            return null;
        }
        VerifiedEvent event = (VerifiedEvent) obj;
        List<EcoRewardRule> ruleList = ecoRewardRuleService.queryEcoRewardRuleByEventName(eventName);
        EcoRewardRule rule = ruleList.get(0);
        if (rule.getScenario().getRestrictionType() == RestrictionType.ID) {
            Optional<String> idHashOpt = chainOrderService.getIdHash(event.getAddress());
            if (idHashOpt.isPresent()) {
                IdRestriction idRestriction = idRestrictionRepository.findByScenarioIdAndIdHash(rule.getId(), idHashOpt.get());
                if (idRestriction != null) {
                    return null;
                }
            } else {
                return null;
            }
        }





        MiningRewardRecord rewardRecord = new MiningRewardRecord();
        rewardRecord.setScore(ecoRewardRule.getScore());
        rewardRecord.setAddress(event.getAddress());
        rewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
        miningRewardRecordService.saveMiningRewardFromEvent(rewardRecord);

        MiningRewardRecord rewardRecord = new MiningRewardRecord();
        rewardRecord.setAddress(event.getAddress());
        rewardRecord.setStatus(Ming);





        //TODO
        return rewardRecord;
    }

    private String C(String address) {
        Member member = wexyunLoanClient.getMemberByIdentity(address);
        if (StringUtils.isNotEmpty(member.getInviteMemberId())) {
            Member inviteMember = wexyunLoanClient.getMemberById(member.getInviteMemberId());
            if (inviteMember != null) {
                return inviteMember.getIdentitys().get(0).getIdentity();
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
}
