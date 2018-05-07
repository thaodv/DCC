package io.wexchain.passport.chain.observer.ext.integration.daemon;

import io.wexchain.passport.chain.observer.domainservice.JuzixReplayService;
import io.wexchain.passport.chain.observer.repository.JuzixBlockRepository;
import io.wexchain.passport.chain.observer.repository.TokenLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

/**
 * ObserverWatcher
 *
 * @author zhengpeng
 */
@Component
public class ObserverWatcher {

    @Autowired
    private TokenLogRepository tokenLogRepository;

    private Logger logger = LoggerFactory.getLogger(ObserverWatcher.class);

    @Value("${observe.contract.address}")
    private String observeContractAddress;

    @Value("${juzix.web3.url}")
    private String web3jUrl;

    @Autowired
    private Web3j web3j;

    @Autowired
    private JuzixReplayService juzixReplayService;

    @Autowired
    private JuzixBlockRepository juzixBlockRepository;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        try {
            Long maxNumber = juzixBlockRepository.findMaxNumber();
            maxNumber = maxNumber == null ? -1 : maxNumber;
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            if (ethBlockNumber.getBlockNumber().longValue() - maxNumber >= 2) {
                logger.info("Restart observer");
                juzixReplayService.startReplayAndObserver();
            }
        } catch (Exception e) {
            logger.error("Restart observer error", e);
        }

    }
}
