package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.cert.sdk.service.CertService;
import io.wexchain.dcc.marketing.api.constant.*;
import io.wexchain.dcc.marketing.api.model.request.*;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.Scenario;
import io.wexchain.dcc.marketing.domainservice.RedeemTokenService;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.RedeemTokenRepository;
import io.wexchain.dcc.marketing.repository.query.RedeemTokenQueryBuilder;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RedeemTokenServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RedeemTokenServiceImpl implements RedeemTokenService {

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private RedeemTokenRepository redeemTokenRepository;

    @Autowired
    private IdRestrictionRepository idRestrictionRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Resource(name = "redeemTokenExecutor")
    private OrderExecutor<RedeemToken, RedeemTokenInstruction> redeemTokenExecutor;

    @Autowired
    private CertService certService;

    @Autowired
    private ChainOrderService chainOrderService;

    @Override
    public RedeemTokenQualification getRedeemTokenQualification(GetRedeemTokenQualificationRequest request) {
        Scenario scenario = scenarioService.getScenario(request.getScenarioCode());
        if (redeemTokenRepository.findByScenarioCodeAndReceiverAddress(
                scenario.getCode(), request.getAddress()) != null) {
            return RedeemTokenQualification.REDEEMED;
        }
        if (scenario.getRestrictionType() == RestrictionType.ID) {
            IdRestriction idRestriction = idRestrictionRepository.findByScenarioIdAndIdHash(
                    scenario.getId(), request.getIdHash());
            if (idRestriction != null) {
                return RedeemTokenQualification.RESTRICTED;
            }
        }
        return RedeemTokenQualification.AVAILABLE;
    }

    @Override
    public RedeemToken redeemToken(RedeemTokenRequest request) {
        Scenario scenario = scenarioService.getScenario(request.getScenarioCode());
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.CREATED,
                MarketingErrorCode.ACTIVITY_IS_OFFLINE);
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.ENDED,
                MarketingErrorCode.ACTIVITY_IS_ENDED);

        RedeemToken redeemToken = transactionTemplate.execute(status -> {
            RedeemToken rt = new RedeemToken();
            rt.setScenario(scenario);
            rt.setReceiverAddress(request.getAddress());
            rt.setStatus(RedeemTokenStatus.CREATED);
            rt.setAmount(scenario.getAmount());
            if (scenario.getRestrictionType() == RestrictionType.ID) {
                IdRestriction idRestriction = new IdRestriction();
                idRestriction.setScenario(scenario);
                idRestriction.setIdHash(request.getIdHash());
                idRestriction = idRestrictionRepository.save(idRestriction);
                rt.setRestrictionId(idRestriction.getId());
            }
            return redeemTokenRepository.save(rt);
        });
        redeemTokenExecutor.executeAsync(redeemToken, null, null);
        return redeemToken;
    }

    @Override
    public List<RedeemToken> queryRedeemToken(QueryRedeemTokenRequest request) {
        /*List<RedeemToken> list = new ArrayList<>();
        for (String scenario : request.getScenarioCodeList()) {
            RedeemToken redeemToken = redeemTokenRepository.findByScenarioCodeAndReceiverAddress(
                    scenario, request.getAddress());
            if (redeemToken != null) {
                list.add(redeemToken);
            }
        }
        return list;*/
        return redeemTokenRepository.findAll(RedeemTokenQueryBuilder.query(request));
    }

    @Override
    public List<IdRestriction> queryIdRestriction(QueryIdRestrictionRequest request) {
        List<IdRestriction> list = new ArrayList<>();
        for (String scenario : request.getScenarioCodeList()) {
            IdRestriction idRestriction = idRestrictionRepository.findByScenarioCodeAndIdHash(
                    scenario, request.getIdHash());
            if (idRestriction != null) {
                list.add(idRestriction);
            }
        }
        return list;
    }

    @Override
    public RedeemToken createBonus(RedeemTokenRequest request) {
        Scenario scenario = scenarioService.getScenario(request.getActivityCode(), request.getScenarioCode());

        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.CREATED,
                MarketingErrorCode.ACTIVITY_IS_OFFLINE);
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.ENDED,
                MarketingErrorCode.ACTIVITY_IS_ENDED);

        Optional<String> idHash = chainOrderService.getIdHash(request.getAddress());
        return transactionTemplate.execute(status -> {
            RedeemToken rt = new RedeemToken();
            rt.setScenario(scenario);
            rt.setReceiverAddress(request.getAddress());
            rt.setStatus(RedeemTokenStatus.CREATED);
            if (scenario.getRestrictionType() == RestrictionType.ID) {
                String idHashStr = idHash.orElseThrow(() -> new ContextedRuntimeException("id_hash_not_found"));
                IdRestriction idRestriction = new IdRestriction();
                idRestriction.setScenario(scenario);
                idRestriction.setIdHash(idHashStr);
                idRestriction = idRestrictionRepository.save(idRestriction);
                rt.setRestrictionId(idRestriction.getId());
            }
            return redeemTokenRepository.save(rt);
        });
    }

    @Override
    public RedeemToken applyBonus(ApplyBonusRequest request) {
        Scenario scenario = scenarioService.getScenario(request.getActivityCode(), request.getScenarioCode());
        RedeemToken redeemToken = redeemTokenRepository.findByIdAndScenarioIdAndReceiverAddress(request.getRedeemTokenId(),
                scenario.getId(), request.getAddress());
        return redeemTokenExecutor.execute(redeemToken, null, null).getModel();
    }
}
