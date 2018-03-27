package io.wexchain.passport.chain.observer.domainservice.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.chain.observer.common.model.ChartPoint;
import io.wexchain.passport.chain.observer.common.model.TransactionInputData;
import io.wexchain.passport.chain.observer.common.request.QueryJuzixTransactionRequest;
import io.wexchain.passport.chain.observer.common.util.PageUtil;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import io.wexchain.passport.chain.observer.domainservice.JuzixTokenTransferService;
import io.wexchain.passport.chain.observer.domainservice.JuzixTransactionService;
import io.wexchain.passport.chain.observer.repository.JuzixTransactionRepository;
import io.wexchain.passport.chain.observer.repository.query.JuzixTransactionQueryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.wexchain.passport.chain.observer.common.constant.ChainErrorCode.TRANSACTION_NOT_FOUND;

/**xk
 * JuzixTransactionServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class JuzixTransactionServiceImpl implements JuzixTransactionService {

    @Autowired
    private JuzixTransactionRepository juzixTransactionRepository;

    @Autowired
    private JuzixTokenTransferService juzixTokenTransferService;

    @Autowired
    private Web3j web3j;

    @Value("#{'${juzix.contract.abi.paths}'.split(',')}")
    private List<String> contractAbiPathList;

    private Map<String, String> contractAbiMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(contractAbiPathList)) {
            for (String abiPath : contractAbiPathList) {
                try {
                    File abiFile = new File(abiPath);
                    String abi = FileUtils.readFileToString(abiFile, "UTF-8");
                    String name = abiFile.getName();
                    contractAbiMap.put(name.substring(4, name.lastIndexOf(".")), abi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public JuzixTransaction getJuzixTransactionByHash(String hash) {
        String lowerCaseHash = hash.toLowerCase();
        if (!lowerCaseHash.startsWith("0x")) {
            lowerCaseHash = "0x" + lowerCaseHash;
        }
        JuzixTransaction transaction = ErrorCodeValidate.notNull(juzixTransactionRepository.findByHash(lowerCaseHash), TRANSACTION_NOT_FOUND);
        //transaction.setInputData(getTransactionInputData(transaction.getHash()));
        transaction.setDccValue(juzixTokenTransferService.getDccTotalValue(transaction.getHash()));
        return transaction;
    }

    @Override
    public JuzixTransaction getJuzixTransactionByHashNullable(String hash) {
        String lowerCaseHash = hash.toLowerCase();
        if (!lowerCaseHash.startsWith("0x")) {
            lowerCaseHash = "0x" + lowerCaseHash;
        }
        JuzixTransaction transaction = juzixTransactionRepository.findByHash(lowerCaseHash);
        if (transaction != null) {
            //String data = getTransactionInputData(transaction.getHash());
           // transaction.setInputData(data);
        }
        return transaction;
    }

    @Override
    public Page<JuzixTransaction> queryJuzixTransaction(QueryJuzixTransactionRequest request) {
        PageRequest pageRequest = PageUtil.convert(request, new Sort(Sort.Direction.DESC, "blockTimestamp"));
        Page<JuzixTransaction> page = juzixTransactionRepository.findAll(JuzixTransactionQueryBuilder.queryTransaction(request), pageRequest);
        page.getContent().forEach(tx -> {
            tx.setDccValue(juzixTokenTransferService.getDccTotalValue(tx.getHash()));
        });
        return page;
    }

    @Override
    public List<ChartPoint<Integer, Integer>> getTransactionStatistics(int hours) {
        List<ChartPoint<Integer, Integer>> pointList = new ArrayList<>();
        DateTime now = new DateTime();
        DateTime pastTime = now.minusHours(hours - 1);
        for (int i = 0; i < hours; i++) {
            DateTime start = pastTime.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime end = pastTime.withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
            int txn = juzixTransactionRepository.countByBlockTimestampBetween(start.toDate(), end.toDate());
            ChartPoint<Integer, Integer> point = new ChartPoint<>(pastTime.getHourOfDay(), txn);
            pointList.add(point);
            pastTime = pastTime.plusHours(1);
        }
        return pointList;
    }

    @Override
    public long getTotalTransactionNumber() {
        return juzixTransactionRepository.count();
    }

    @Override
    public TransactionInputData getTransactionInputData(String hash) {
        try {
            EthTransaction tx = web3j.ethGetTransactionByHash(hash).send();
            TransactionInputData inputData = new TransactionInputData();
            inputData.setContractAddress(tx.getResult().getTo());
            inputData.setInputData(tx.getResult().getInput());
            inputData.setAbi(contractAbiMap.get(tx.getResult().getTo()));
            return inputData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
