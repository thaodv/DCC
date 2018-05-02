package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.marketing.api.constant.*;
import io.wexchain.dcc.marketing.api.model.request.GetRedeemTokenQualificationRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.marketing.api.model.request.RedeemTokenRequest;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.Scenario;
import io.wexchain.dcc.marketing.domainservice.RedeemTokenService;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.ext.integration.web3.ParameterizedToken;
import io.wexchain.dcc.marketing.ext.integration.web3.TestToken;
import io.wexchain.dcc.marketing.repository.IdRestrictionRepository;
import io.wexchain.dcc.marketing.repository.RedeemTokenRepository;
import juzix.web3j.NonceRawTransactionManager;
import juzix.web3j.protocol.CustomWeb3j;
import juzix.web3j.protocol.CustomWeb3jFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.http.HttpService;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private static BigInteger GAS_LIMIT = new BigInteger("999999999999");
    private static BigInteger GAS_PRICE = new BigInteger("210000000000");
    private static final String TRANSFER_METHOD_HASH = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    public static void main(String[] args) {
        try {
            CustomWeb3j customWeb3j = CustomWeb3jFactory.buildweb3j(new HttpService("http://10.65.209.49:6789"));
            Credentials credentials = WalletUtils.loadCredentials("abc123", "D:/work/conf/dcc-marketing/wallet-5a4d17363a58411fe2bf84097f805df4db2c68d6.json");
            ParameterizedToken dccToken = ParameterizedToken.load(
                    "0x625769a40adcd290591cd4a83eae25d374099d4d", customWeb3j,
                    new NonceRawTransactionManager(customWeb3j, credentials),
                    GAS_PRICE, GAS_LIMIT);
            Uint256 uint256 = dccToken.allowance(new Address("0xdb8db4eece7dbb3a0def2e1081392358944e476f"), new Address(credentials.getAddress())).get();
            System.out.println(uint256.getValue());
            /*dccToken.transfer(new Address("0x123"), new Uint256(new BigInteger("100"))).get();
            dccToken.transfer(new Address("0x123"), new Uint256(new BigInteger("100"))).get();
            Uint256 uint256 = dccToken.balanceOf(new Address("0x123")).get();
            System.out.println(uint256.getValue());*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
        List<RedeemToken> list = new ArrayList<>();
        for (String scenario : request.getScenarioCodeList()) {
            RedeemToken redeemToken = redeemTokenRepository.findByScenarioCodeAndReceiverAddress(
                    scenario, request.getAddress());
            if (redeemToken != null) {
                list.add(redeemToken);
            }
        }
        return list;
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
}
