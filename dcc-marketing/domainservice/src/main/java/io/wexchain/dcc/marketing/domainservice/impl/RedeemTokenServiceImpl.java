package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.marketing.api.constant.*;
import io.wexchain.dcc.marketing.api.model.request.*;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.Scenario;
import io.wexchain.dcc.marketing.domainservice.Patroller;
import io.wexchain.dcc.marketing.domainservice.RedeemTokenService;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.RedeemTokenRepository;
import io.wexchain.dcc.marketing.repository.query.RedeemTokenQueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * RedeemTokenServiceImpl
 *
 * @author zhengpeng
 */
@Service("redeemTokenService")
public class RedeemTokenServiceImpl implements RedeemTokenService, Patroller {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    private ChainOrderService chainOrderService;

    @Override
    public RedeemTokenQualification getRedeemTokenQualification(GetRedeemTokenQualificationRequest request) {
        Scenario scenario = scenarioService.getScenarioByCode(request.getScenarioCode());
        if (redeemTokenRepository.findByScenarioCodeAndReceiverAddress(
                scenario.getCode(), request.getAddress()) != null) {
            return RedeemTokenQualification.REDEEMED;
        }
        if (scenario.getRestrictionType() == RestrictionType.ID) {
            String idHash = prepareIdHash(scenario, request.getAddress(), request.getIdHash());
            IdRestriction idRestriction = idRestrictionRepository.findByScenarioIdAndIdHash(
                    scenario.getId(), idHash);
            if (idRestriction != null) {
                return RedeemTokenQualification.RESTRICTED;
            }
        }
        return RedeemTokenQualification.AVAILABLE;
    }

    @Override
    public RedeemToken redeemToken(RedeemTokenRequest request) {
        Scenario scenario = scenarioService.getScenarioByCode(request.getScenarioCode());
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.CREATED,
                MarketingErrorCode.ACTIVITY_IS_OFFLINE);
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.ENDED,
                MarketingErrorCode.ACTIVITY_IS_ENDED);

        String idHash = prepareIdHash(scenario, request.getAddress(), request.getIdHash());
        RedeemToken redeemToken = transactionTemplate.execute(status -> {
            RedeemToken rt = new RedeemToken();
            rt.setScenario(scenario);
            rt.setReceiverAddress(request.getAddress());
            rt.setStatus(RedeemTokenStatus.CREATED);
            rt.setAmount(scenario.getAmount());
            if (scenario.getRestrictionType() == RestrictionType.ID) {
                IdRestriction idRestriction = saveIdRestriction(scenario, idHash);
                rt.setRestrictionId(idRestriction.getId());
            }
            return redeemTokenRepository.save(rt);
        });
        redeemTokenExecutor.executeAsync(redeemToken, null, null);
        return redeemToken;
    }

    @Override
    public List<RedeemToken> queryRedeemToken(QueryRedeemTokenRequest request) {
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
        Scenario scenario = scenarioService.getScenarioByCode(request.getActivityCode(), request.getScenarioCode());

        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.CREATED,
                MarketingErrorCode.ACTIVITY_IS_OFFLINE);
        ErrorCodeValidate.isTrue(
                scenario.getActivity().getStatus() != ActivityStatus.ENDED,
                MarketingErrorCode.ACTIVITY_IS_ENDED);

        String idHash = prepareIdHash(scenario, request.getAddress(), request.getIdHash());

        return transactionTemplate.execute(status -> {
            RedeemToken rt = new RedeemToken();
            rt.setScenario(scenario);
            rt.setReceiverAddress(request.getAddress());
            rt.setStatus(RedeemTokenStatus.CREATED);
            if (scenario.getRestrictionType() == RestrictionType.ID) {
                IdRestriction idRestriction = saveIdRestriction(scenario, idHash);
                rt.setRestrictionId(idRestriction.getId());
            }
            return redeemTokenRepository.save(rt);
        });
    }

    @Override
    public RedeemToken applyBonus(ApplyBonusRequest request) {
        Scenario scenario = scenarioService.getScenarioByCode(request.getActivityCode(), request.getScenarioCode());
        RedeemToken redeemToken = redeemTokenRepository.findByIdAndScenarioIdAndReceiverAddress(request.getRedeemTokenId(),
                scenario.getId(), request.getAddress());
        return redeemTokenExecutor.execute(redeemToken, null, null).getModel();
    }

    @Override
    public void patrol() {

        DateTime now = new DateTime();
        Date beginTime = now.minusDays(60).toDate();
        Date endTime = now.minusMinutes(1).toDate();

        List<RedeemToken> list = redeemTokenRepository
                .findTop1000ByStatusInAndCreatedTimeBetweenOrderByIdAsc(
                 Collections.singletonList(RedeemTokenStatus.DECIDED), beginTime, endTime);
        logger.info("Patrol redeem token size: {}", list.size());

        for (RedeemToken redeemToken : list) {
            redeemTokenExecutor.executeAsync(redeemToken, null, null);
        }
    }

    /**
     * 获取ID HASH, 先从请求参数中获取，取不到从链上获取
     */
    private String prepareIdHash(Scenario scenario, String address, String inputIdHash) {
        if (scenario.getRestrictionType() == RestrictionType.ID) {
            if (StringUtils.isNotEmpty(inputIdHash)) {
                return inputIdHash;
            }
            return chainOrderService.getIdHash(address).orElseThrow(()
                    -> new ErrorCodeException("ID_HASH_NOT_EXIST", "ID HASH不存在"));
        }
        return null;
    }

    private IdRestriction saveIdRestriction(Scenario scenario, String idHash) {
        IdRestriction idRestriction = new IdRestriction();
        idRestriction.setScenario(scenario);
        idRestriction.setIdHash(idHash);
        return idRestrictionRepository.save(idRestriction);
    }
}
