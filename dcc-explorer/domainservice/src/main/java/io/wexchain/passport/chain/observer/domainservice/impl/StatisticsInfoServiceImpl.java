package io.wexchain.passport.chain.observer.domainservice.impl;

import io.wexchain.passport.chain.observer.common.model.ChartPoint;
import io.wexchain.passport.chain.observer.common.model.DccStatisticsInfo;
import io.wexchain.passport.chain.observer.common.model.StatisticsInfo;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.domainservice.JuzixTransactionService;
import io.wexchain.passport.chain.observer.domainservice.StatisticsInfoService;
import io.wexchain.passport.chain.observer.domainservice.function.juzix.ConsensusTimeHelper;
import jodd.bean.BeanCopy;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.acl.LastOwnerException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * StatisticsInfoServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class StatisticsInfoServiceImpl implements StatisticsInfoService {

    @Autowired
    private Web3j web3j;

    @Autowired
    private JuzixTransactionService juzixTransactionService;

    @Autowired
    private JuzixBlockService juzixBlockService;

    @Autowired
    private ConsensusTimeHelper consensusTimeHelper;

    @Value("${juzix.contract.address.token.dcc}")
    private String dccTokenContractAddress;

    private static final String SENDER_ADDRESS = "0x0000000000000000000000000000000000000000";

    private final static BigDecimal SCALE = new BigDecimal("10").pow(18);

    @PostConstruct
    public void init() {
        web3j.ethPendingTransactionHashObservable().subscribe(hash -> {
            consensusTimeHelper.addPendingHash(hash);
        });
    }

    @Override
    public StatisticsInfo getStatisticsInfo() {
        try {
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            long totalTransactionNumber = juzixTransactionService.getTotalTransactionNumber();
            List<ChartPoint<Integer, Integer>> transactionStatistics = juzixTransactionService.getTransactionStatistics(24);
            StatisticsInfo info = new StatisticsInfo();
            info.setBlockNumber(blockNumber.getBlockNumber().longValue());
            info.setTrendList(transactionStatistics);
            info.setTotalTransactionNumber(totalTransactionNumber);
            info.setNodeNumber(web3j.netPeerCount().send().getQuantity().intValue() + 1);
            info.setGeneBlockTime(juzixBlockService.getLatestGeneBlockTime());
            info.setConsensusTime(consensusTimeHelper.getLatestConsensusTime());

            info.setTransactionPerDay(juzixTransactionService.getTransactionPerDay());
            info.setDccTotalSupply(getTotalSupply());

            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DccStatisticsInfo getDccStatisticsInfo() {
        try {
            DccStatisticsInfo info = new DccStatisticsInfo();
            info.setTransactionPerDay(juzixTransactionService.getTransactionPerDay());
            info.setDccTotalSupply(getTotalSupply());
            return info;
        } catch (Exception e) {
            throw new ContextedRuntimeException(e);
        }
    }

    private BigDecimal getTotalSupply() {

        try {
            Function function = new Function("totalSupply",
                    Collections.emptyList(),
                    Collections.singletonList(new TypeReference<Uint256>() {}));
            String encodedFunction = FunctionEncoder.encode(function);

            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(SENDER_ADDRESS, dccTokenContractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST)
                    .sendAsync().get();

            List<Type> someTypes = FunctionReturnDecoder.decode(
                    response.getValue(), function.getOutputParameters());

            BigInteger totalSupplyWei = new BigInteger(someTypes.get(0).getValue() + "");
            return new BigDecimal(totalSupplyWei).divide(SCALE, BigDecimal.ROUND_DOWN);

        } catch (Exception e) {
            throw new ContextedRuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Web3j build = Web3j.build(new HttpService("http://101.227.190.83:6789"));
        try {


            EthGetTransactionReceipt receipt = build.ethGetTransactionReceipt("0x43fe4660f64fb3753ace4d12ae5a722615aa3134be7db4a595186d3172685a06").send();
            TransactionReceipt result = receipt.getResult();
            Log from = receipt.getResult().getLogs().get(0);

            List<Log> logs = new ArrayList<>();


            for (int i = 0; i < 52000; i++) {
                Log log =  new Log();
                log.setAddress(from.getAddress());
                log.setBlockHash(from.getBlockHash());
                log.setData(from.getData());
                log.setTransactionHash(from.getTransactionHash());
                logs.add(log);
                System.out.println(i);
            }


            Thread.sleep(60 * 1000);

            System.out.println(logs.size());


            /*Function function = new Function("owner",
                    Collections.emptyList(),
                    Collections.singletonList(new TypeReference<Address>() {}));*/

            /*Function function = new Function("getOwner",
                    Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));

            String encodedFunction = FunctionEncoder.encode(function);

            EthCall response = build.ethCall(
                    Transaction.createEthCallTransaction(SENDER_ADDRESS,
                            "0x0d3729db1b10a1238f2ab84851e05de7ef57c274", encodedFunction),
                    DefaultBlockParameterName.LATEST)
                    .send();

            List<Type> someTypes = FunctionReturnDecoder.decode(
                    response.getValue(), function.getOutputParameters());

            System.out.println(someTypes);*/

        } catch (Exception e) {
            throw new ContextedRuntimeException(e);
        }

    }
}
