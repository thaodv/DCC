package io.wexchain.passport.chain.observer.domainservice.impl;

import io.wexchain.passport.chain.observer.common.constant.AddressType;
import io.wexchain.passport.chain.observer.common.constant.JuzixTxStatus;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import io.wexchain.passport.chain.observer.domainservice.JuzixReplayService;
import io.wexchain.passport.chain.observer.domainservice.function.juzix.ConsensusTimeHelper;
import io.wexchain.passport.chain.observer.repository.JuzixBlockRepository;
import io.wexchain.passport.chain.observer.repository.JuzixTokenTransferRepository;
import io.wexchain.passport.chain.observer.repository.JuzixTransactionRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JuzixReplayServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class JuzixReplayServiceImpl implements JuzixReplayService {

    private Logger logger = LoggerFactory.getLogger(JuzixReplayServiceImpl.class);

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Web3j web3j;

    @Autowired
    private JuzixBlockRepository juzixBlockRepository;

    @Autowired
    private JuzixTransactionRepository juzixTransactionRepository;

    public static final String TRANSFER_METHOD_HASH = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    @Autowired
    private JuzixTokenTransferRepository juzixTokenTransferRepository;

    @Autowired
    private ConsensusTimeHelper consensusTimeHelper;

    @PostConstruct
    public void init() {
        try {
            Long maxNumber = juzixBlockRepository.findMaxNumber();
            maxNumber = maxNumber == null ? -1 : maxNumber;
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            logger.info("Preparing start replay blocks and transactions, DB max block number:{}, ETH max block number:{}",
                    juzixBlockRepository, ethBlockNumber.getBlockNumber());
            if (ethBlockNumber.getBlockNumber().longValue() > maxNumber) {
                logger.info("Start replay blocks and transactions, start block number:{}", maxNumber);
                replayBlocksAndTransactions(maxNumber + 1, null);
            }

            observeBlock();

        } catch (IOException e) {
            logger.error("Start replay block error", e);
        }
    }

    private void observeBlock() {

        web3j.blockObservable(true).subscribe(log -> {
            EthBlock.Block block = log.getBlock();
            if (block != null) {
                try {
                    transactionTemplate.execute(status -> {
                        // Save block
                        JuzixBlock juzixBlock = buildBlock(block);
                        juzixBlock = juzixBlockRepository.save(juzixBlock);
                        // Save transactions
                        List<EthBlock.TransactionResult> transactionList = block.getTransactions();
                        if (CollectionUtils.isNotEmpty(transactionList)) {
                            for (EthBlock.TransactionResult transactionResult : transactionList) {
                                // calc
                                EthBlock.TransactionObject et = (EthBlock.TransactionObject) transactionResult;
                                consensusTimeHelper.chained(et.getHash());

                                // Save transaction
                                JuzixTransaction juzixTransaction =
                                        buildTransaction(juzixBlock, (EthBlock.TransactionObject) transactionResult.get());
                                putAddressType(juzixTransaction);
                                EthGetTransactionReceipt transactionReceipt = getTransactionReceipt(juzixTransaction.getHash());
                                juzixTransaction = putTransactionStatus(juzixTransaction, transactionReceipt);
                                juzixTransaction = juzixTransactionRepository.save(juzixTransaction);

                                // Save token transfer logs
                                List<JuzixTokenTransfer> tokenTransferList = getTokenTransferList(juzixTransaction, transactionReceipt);
                                juzixTokenTransferRepository.save(tokenTransferList);
                            }
                        }
                        return juzixBlock;
                    });
                } catch (Exception e) {
                    logger.error("Build or save replay block error, block number:{}, block hash:{}",
                            block.getNumber(), block.getHash(), e);
                }
            }
        });

    }

    @Override
    public void replayBlocksAndTransactions(Long startBlockNumber, Long endBlockNumber) {
        DefaultBlockParameter start;
        DefaultBlockParameter end;
        if (startBlockNumber == null) {
            start = DefaultBlockParameterName.EARLIEST;
        } else {
            start = DefaultBlockParameter.valueOf(new BigInteger(String.valueOf(startBlockNumber)));
        }
        if (endBlockNumber == null) {
            end = DefaultBlockParameterName.LATEST;
        } else {
            end = DefaultBlockParameter.valueOf(new BigInteger(String.valueOf(endBlockNumber)));
        }


        web3j.replayBlocksObservable(start, end, true, true).subscribe(log -> {
            EthBlock.Block block = log.getBlock();
            if (block != null) {
                try {
                    transactionTemplate.execute(status -> {
                        // Save block
                        JuzixBlock juzixBlock = buildBlock(block);
                        juzixBlock = juzixBlockRepository.save(juzixBlock);
                        // Save transactions
                        List<EthBlock.TransactionResult> transactionList = block.getTransactions();
                        if (CollectionUtils.isNotEmpty(transactionList)) {
                            for (EthBlock.TransactionResult transactionResult : transactionList) {
                                // Save transaction
                                JuzixTransaction juzixTransaction =
                                        buildTransaction(juzixBlock, (EthBlock.TransactionObject) transactionResult.get());
                                putAddressType(juzixTransaction);
                                EthGetTransactionReceipt transactionReceipt = getTransactionReceipt(juzixTransaction.getHash());
                                juzixTransaction = putTransactionStatus(juzixTransaction, transactionReceipt);
                                juzixTransaction = juzixTransactionRepository.save(juzixTransaction);

                                // Save token transfer logs
                                List<JuzixTokenTransfer> tokenTransferList = getTokenTransferList(juzixTransaction, transactionReceipt);
                                juzixTokenTransferRepository.save(tokenTransferList);
                            }
                        }
                        return juzixBlock;
                    });
                } catch (Exception e) {
                    logger.error("Build or save replay block error, block number:{}, block hash:{}",
                            block.getNumber(), block.getHash(), e);
                }
            }
        });
    }

    private JuzixBlock buildBlock(EthBlock.Block block) {
        JuzixBlock juzixBlock = new JuzixBlock();
        juzixBlock.setAuthor(block.getAuthor());
        juzixBlock.setBlockNumber(block.getNumber().longValue());
        juzixBlock.setBlockTimestamp(new Date(block.getTimestamp().longValue() * 1000));
        juzixBlock.setBlockSize(block.getSizeRaw() == null ? null : block.getSize().longValue());
        juzixBlock.setDifficulty(block.getDifficultyRaw() == null ? null : block.getDifficulty());
        juzixBlock.setTotalDifficulty(block.getTotalDifficultyRaw() == null ? null : block.getTotalDifficulty());
        juzixBlock.setGasLimit(block.getGasLimit());
        juzixBlock.setGasUsed(block.getGasUsed());
        juzixBlock.setHash(block.getHash());
        juzixBlock.setParentHash(block.getParentHash());
        juzixBlock.setMiner(block.getMiner());
        juzixBlock.setMixHash(block.getMixHash());
        juzixBlock.setNonce(block.getNonceRaw());
        juzixBlock.setReceiptsRoot(block.getReceiptsRoot());
        juzixBlock.setStateRoot(block.getStateRoot());
        juzixBlock.setTransactionCount(CollectionUtils.size(block.getTransactions()));
        juzixBlock.setTransactionsRoot(block.getTransactionsRoot());
        juzixBlock.setExtraData(block.getExtraData());
        return juzixBlock;
    }

    private JuzixTransaction buildTransaction(JuzixBlock block, EthBlock.TransactionObject transactionObject) {
        JuzixTransaction tx = new JuzixTransaction();
        tx.setBlock(block);
        tx.setNonce(transactionObject.getNonceRaw());
        tx.setBlockHash(transactionObject.getBlockHash());
        tx.setBlockNumber(transactionObject.getBlockNumber().longValue());
        tx.setFromAddress(transactionObject.getFrom());
        tx.setToAddress(transactionObject.getTo());
        tx.setGas(transactionObject.getGas());
        tx.setGasPrice(transactionObject.getGasPrice());
        tx.setHash(transactionObject.getHash());
        tx.setTransactionIndex(transactionObject.getTransactionIndex().intValue());
        tx.setValue(transactionObject.getValue());
        tx.setBlockTimestamp(block.getBlockTimestamp());
        return tx;
    }


    private void putAddressType(JuzixTransaction transaction) {
        int retryTimes = 5;
        for (int i = 0; i < retryTimes; i++) {
            try {

                if (StringUtils.isNotEmpty(transaction.getFromAddress())) {
                    EthGetCode fromCode = web3j.ethGetCode(transaction.getFromAddress(), DefaultBlockParameterName.LATEST).send();
                    if (fromCode.getError() != null) {
                        Thread.sleep(1000);
                        continue;
                    }
                    if ("0x".equalsIgnoreCase(fromCode.getResult())) {
                        transaction.setFromType(AddressType.EXTERNALLY_OWNED);
                    } else {
                        transaction.setFromType(AddressType.CONTRACT);
                    }
                } else if (StringUtils.isNotEmpty(transaction.getToAddress())) {
                    EthGetCode toCode = web3j.ethGetCode(transaction.getToAddress(), DefaultBlockParameterName.LATEST).send();
                    if (toCode.getError() != null) {
                        Thread.sleep(1000);
                        continue;
                    }
                    if ("0x".equalsIgnoreCase(toCode.getResult())) {
                        transaction.setToType(AddressType.EXTERNALLY_OWNED);
                    } else {
                        transaction.setToType(AddressType.CONTRACT);
                    }
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private JuzixTransaction putTransactionStatus(JuzixTransaction juzixTransaction,
                                                  EthGetTransactionReceipt transactionReceipt) {
        if (StringUtils.isNotEmpty(transactionReceipt.getResult().getStatus())) {
            if ("0x1".equalsIgnoreCase(transactionReceipt.getResult().getStatus())) {
                juzixTransaction.setStatus(JuzixTxStatus.SUCCESS);
            } else {
                juzixTransaction.setStatus(JuzixTxStatus.FAIL);
            }
        }
        return juzixTransaction;
    }

    private List<JuzixTokenTransfer> getTokenTransferList(JuzixTransaction juzixTransaction,
                                                          EthGetTransactionReceipt transactionReceipt) {
        List<Log> logList = transactionReceipt.getResult().getLogs();
        List<JuzixTokenTransfer> tokenTransferList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logList)) {
            for (Log log : logList) {
                List<String> topicList = log.getTopics();
                if (CollectionUtils.isNotEmpty(topicList)) {
                    if (TRANSFER_METHOD_HASH.equalsIgnoreCase(topicList.get(0))) {
                        JuzixTokenTransfer tokenTransfer = new JuzixTokenTransfer();
                        tokenTransfer.setTransactionHash(log.getTransactionHash());
                        tokenTransfer.setTransactionIndex(Integer.valueOf(log.getTransactionIndexRaw()));
                        tokenTransfer.setBlockHash(log.getBlockHash());
                        tokenTransfer.setBlockNumber(Long.valueOf(log.getBlockNumberRaw()));
                        tokenTransfer.setBlockTimestamp(juzixTransaction.getBlock().getBlockTimestamp());
                        tokenTransfer.setLogIndex(Integer.valueOf(log.getLogIndexRaw()));
                        tokenTransfer.setFromAddress(topicList.get(1).replace("0x000000000000000000000000", "0x"));
                        tokenTransfer.setToAddress(topicList.get(2).replace("0x000000000000000000000000", "0x"));
                        tokenTransfer.setContractAddress(log.getAddress());
                        tokenTransfer.setValue(new BigInteger(log.getData().substring(2), 16));
                        tokenTransfer.setTransaction(juzixTransaction);
                        tokenTransferList.add(tokenTransfer);
                    }
                }
            }
        }
        return tokenTransferList;
    }

}
