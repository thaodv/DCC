package io.wexchain.passport.chain.observer.domainservice;

/**
 * JuzixReplayService
 *
 * @author zhengpeng
 */
public interface JuzixReplayService {

    /**
     * 重放区块和交易
     * @param startBlockNumber 起始区块高度
     * @param endBlockNumber 结束区块高度
     */
    void replayBlocksAndTransactions(Long startBlockNumber, Long endBlockNumber);

}
