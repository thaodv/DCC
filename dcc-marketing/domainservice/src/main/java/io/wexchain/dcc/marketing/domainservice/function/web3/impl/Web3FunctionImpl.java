package io.wexchain.dcc.marketing.domainservice.function.web3.impl;

import io.wexchain.dcc.marketing.domainservice.function.web3.Web3Function;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Web3FunctionImpl
 *
 * @author zhengpeng
 */
@Service
public class Web3FunctionImpl implements Web3Function {

    private Web3j web3j;

    @Value("${juzix.web3.url}")
    private String web3Url;

    @PostConstruct
    public void init() {
        web3j = Web3j.build(new HttpService(web3Url));
    }

    @Override
    public List<EthBlock.Block> getBlockList(long startNumber, long endNumber) {
        try {
            List<EthBlock.Block> list = new ArrayList<>();
            for (long number = startNumber; number <= endNumber; number++) {
                EthBlock ethBlock = web3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(number)), true).send();
                list.add(ethBlock.getBlock());
            }
            return list;
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public List<Log> getEventLogList(long startNumber, long endNumber) {
        try {
            List<Log> logList = new ArrayList<>(100);
            for (long number = startNumber; number <= endNumber; number++) {
                EthBlock.Block block = web3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(number)), true).send().getBlock();
                block.getTransactions().forEach(tx -> {
                    EthBlock.TransactionObject txObj = (EthBlock.TransactionObject) tx;
                    EthGetTransactionReceipt txReceipt = getTransactionReceipt(txObj.getHash());
                    logList.addAll(txReceipt.getResult().getLogs());
                });
            }
            return logList;
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    private EthGetTransactionReceipt getTransactionReceipt(String hash) {
        int retryTimes = 5;
        for (int i = 0; i < retryTimes; i++) {
            try {
                EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(hash).send();
                if (receipt.getError() != null || receipt.getResult() == null) {
                    Thread.sleep(1000);
                    continue;
                }
                return receipt;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Get transaction receipt fail, hash " + hash);
    }

    @Override
    public Long getBlockNumberAfterTime(Date time) {
        try {
            long blockNumber = web3j.ethBlockNumber().send().getBlockNumber().longValue();
            for (long i = blockNumber; i >= 0; i--) {
                EthBlock.Block block = web3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), false).send().getBlock();
                Date blockTime = new Date(block.getTimestamp().longValue() * 1000);
                if (blockTime.before(time)) {
                    return block.getNumber().longValue() + 1;
                }
            }
            return null;
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

}
