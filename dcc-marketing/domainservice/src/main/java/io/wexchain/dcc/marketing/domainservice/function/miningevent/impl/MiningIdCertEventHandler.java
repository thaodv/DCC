package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

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
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.CertOrderStatus;
import io.wexchain.notify.domain.dcc.VerifiedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * MiningEventHandlerImpl
 *
 * @author zhengpeng
 */
public class MiningIdCertEventHandler extends MiningCertEventHandler {

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private WexyunLoanClient wexyunLoanClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MiningRewardRecordRepository miningRewardRecordRepository;

    private static final String BONUS_CODE_ID_CERT_BASIC = "MINING_ID_CERT_PASS_BASIC";
    private static final String BONUS_CODE_ID_CERT_INVITE = "MINING_ID_CERT_PASS_INVITE";
    private static final String BONUS_CODE_ID_CERT_FRIEND = "MINING_ID_CERT_PASS_FRIEND";

    @Override
    public boolean canHandle(Object obj) {
        return super.canHandle(obj) && ((VerifiedEvent) obj).getStatus() == CertOrderStatus.PASSED;
    }

    @Override
    public MiningRewardRecord handle(Object obj) {

        if (!(obj instanceof VerifiedEvent)) {
            return null;
        }
        VerifiedEvent event = (VerifiedEvent) obj;
        List<EcoRewardRule> ruleList = ecoRewardRuleService.queryEcoRewardRuleByEventName(getEventName());
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

        Stream<EcoRewardRule> ruleStream = ruleList.stream();
        EcoRewardRule basicRule = ruleStream.filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_BASIC))
                .findFirst().orElseGet(null);
        EcoRewardRule inviteRule = ruleStream.filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_INVITE))
                .findFirst().orElseGet(null);
        EcoRewardRule friendRule = ruleStream.filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_FRIEND))
                .findFirst().orElseGet(null);


        String inviteAddress = getInviteMemberAddress(event.getAddress());



        transactionTemplate.execute(status -> {

            MiningRewardRecord rewardRecord = new MiningRewardRecord();
            rewardRecord.setAddress(event.getAddress());
            rewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
            rewardRecord.setScore(basicRule.getScore());
            if (StringUtils.isNotEmpty(inviteAddress)) {
                rewardRecord.setScore(inviteRule.getScore());
                MiningRewardRecord friendRecord = new MiningRewardRecord();
                friendRecord.setAddress(inviteAddress);
                friendRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
                friendRecord.setScore(friendRule.getScore());
                friendRecord = miningRewardRecordRepository.save(friendRecord);

            }

zd



            miningRewardRecordService.saveMiningRewardFromEvent(rewardRecord);

            MiningRewardRecord rewardRecord = new MiningRewardRecord();
            rewardRecord.setAddress(event.getAddress());
            rewardRecord.setStatus(Ming);


        })







        //TODO
        return rewardRecord;
    }

    private String getInviteMemberAddress(String address) {
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
