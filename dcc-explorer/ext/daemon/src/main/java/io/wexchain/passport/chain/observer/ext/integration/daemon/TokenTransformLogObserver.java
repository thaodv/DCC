package io.wexchain.passport.chain.observer.ext.integration.daemon;

import io.wexchain.passport.chain.observer.domain.TokenLog;
import io.wexchain.passport.chain.observer.domainservice.impl.JuzixReplayServiceImpl;
import io.wexchain.passport.chain.observer.repository.TokenLogRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * TokenTransformLogObserver
 *
 * @author zhengpeng
 */
@Component
public class TokenTransformLogObserver {

    @Autowired
    private TokenLogRepository tokenLogRepository;

    private Logger logger = LoggerFactory.getLogger(TokenTransformLogObserver.class);

    @Value("${observe.contract.address}")
    private String observeContractAddress;

    @Value("${juzix.web3.url}")
    private String web3jUrl;

    public void init() {

        if (StringUtils.isBlank(observeContractAddress)) {
            logger.error("矩阵元监听合约地址为空");
            return;
        }

        List<String> addressList = Arrays.asList(observeContractAddress.split(","));
        logger.info("获取矩阵元交易监听合约地址，合约数量:{}", addressList.size());

        Web3j web3j = Web3j.build(new HttpService(web3jUrl)); // defaults
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, addressList);

        logger.info("开始监听");
        Subscription subscribe = web3j.ethLogObservable(filter).subscribe(log -> {

            try {
                if (log.getType().equals("mined")) {
                    TokenLog token = tokenLogRepository.findByTransactionHash(log.getTransactionHash());
                    if (token == null) {
                        if (log.getTopics() != null && log.getTopics().get(0).equalsIgnoreCase(JuzixReplayServiceImpl.TRANSFER_METHOD_HASH)) {
                            TokenLog tokenLog = new TokenLog();
                            String fromAddr = log.getTopics().get(1).replace("0x000000000000000000000000", "0x");
                            String toAddr = log.getTopics().get(2).replace("0x000000000000000000000000", "0x");
                            tokenLog.setFromAddress(fromAddr);
                            tokenLog.setToAddress(toAddr);
                            if (StringUtils.isNotEmpty(log.getData())) {
                                tokenLog.setAmount(new BigInteger(log.getData().substring(2), 16));
                            }
                            tokenLog.setBlockHash(log.getBlockHash());
                            tokenLog.setTransactionHash(log.getTransactionHash());
                            tokenLog.setContractAddress(log.getAddress());
                            tokenLog.setType(log.getType());
                            tokenLogRepository.save(tokenLog);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("错误,{}", log.getAddress(), e);
            }

        });
    }
}
