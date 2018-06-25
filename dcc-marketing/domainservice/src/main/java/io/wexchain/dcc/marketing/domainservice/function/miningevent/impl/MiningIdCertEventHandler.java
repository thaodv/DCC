package io.wexchain.dcc.marketing.domainservice.function.miningevent.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.function.wexyun.WexyunLoanClient;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrecord.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import io.wexchain.notify.domain.dcc.VerifiedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * MiningEventHandlerImpl
 *
 * @author zhengpeng
 */
public class MiningIdCertEventHandler extends MiningCertEventHandler {

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private WexyunLoanClient wexyunLoanClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MiningRewardRecordRepository miningRewardRecordRepository;

    @Resource(name = "miningRewardRecordExecutor")
    private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    private static final String BONUS_CODE_ID_CERT_BASIC =  "MINING_ID_CERT_PASS_BASIC";
    private static final String BONUS_CODE_ID_CERT_INVITE = "MINING_ID_CERT_PASS_INVITE";
    private static final String BONUS_CODE_ID_CERT_FRIEND = "MINING_ID_CERT_PASS_FRIEND";

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

        EcoRewardRule basicRule = ruleList.stream().filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_BASIC))
                .findFirst().orElseGet(null);
        EcoRewardRule inviteRule = ruleList.stream().filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_INVITE))
                .findFirst().orElseGet(null);
        EcoRewardRule friendRule = ruleList.stream().filter(r -> r.getBonusCode().equals(BONUS_CODE_ID_CERT_FRIEND))
                .findFirst().orElseGet(null);

        String inviteAddress = getInviteMemberAddress(event.getAddress());

        String idHash1 = idHash;
        List<MiningRewardRecord> recordList = transactionTemplate.execute(status -> {
            List<MiningRewardRecord> list = new ArrayList<>();
            MiningRewardRecord rewardRecord = new MiningRewardRecord();
            rewardRecord.setAddress(event.getAddress());
            rewardRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
            rewardRecord.setScore(basicRule.getScore());
            rewardRecord.setRewardRule(basicRule);
            if (StringUtils.isNotEmpty(inviteAddress)) {
                // 被邀请人额外加分
                rewardRecord.setScore(inviteRule.getScore());
                rewardRecord.setRewardRule(inviteRule);
                // 好友贡献值
                MiningRewardRecord friendRecord = new MiningRewardRecord();
                friendRecord.setAddress(inviteAddress);
                friendRecord.setStatus(MiningActionRecordStatus.ACCEPTED);
                friendRecord.setScore(friendRule.getScore());
                friendRecord.setRewardRule(friendRule);
                list.add(miningRewardRecordRepository.save(friendRecord));
            }
            list.add(miningRewardRecordRepository.save(rewardRecord));

            IdRestriction idRestriction = new IdRestriction();
            idRestriction.setScenario(basicRule.getScenario());
            idRestriction.setIdHash(idHash1);
            idRestrictionRepository.save(idRestriction);

            return list;
        });

        recordList.forEach(record -> miningRwdRecExecutor.executeAsync(record, null, null));

        return null;
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


}
