package io.wexchain.passport.chain.observer.domainservice.function.juzix;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConsensusTimeHelper
 *
 * @author zhengpeng
 */
@Component
public class ConsensusTimeHelper {

    private Map<String, Long> map = new ConcurrentHashMap<>();

    private long latestConsensusTime = 0;

    public void addPendingHash(String hash) {
        map.put(hash, new Date().getTime());
    }

    public void chained(String hash) {
        Long remove = map.remove(hash);
        if (remove != null) {
            latestConsensusTime = new Date().getTime() - remove;
        }
    }

    public long getLatestConsensusTime() {
        return latestConsensusTime;
    }
}
