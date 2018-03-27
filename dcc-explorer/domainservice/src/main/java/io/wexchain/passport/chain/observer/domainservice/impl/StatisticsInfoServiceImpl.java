package io.wexchain.passport.chain.observer.domainservice.impl;

import io.wexchain.passport.chain.observer.common.model.ChartPoint;
import io.wexchain.passport.chain.observer.common.model.StatisticsInfo;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.domainservice.JuzixTransactionService;
import io.wexchain.passport.chain.observer.domainservice.StatisticsInfoService;
import io.wexchain.passport.chain.observer.domainservice.function.juzix.ConsensusTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
