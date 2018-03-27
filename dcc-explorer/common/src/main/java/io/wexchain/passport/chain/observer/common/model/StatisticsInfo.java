package io.wexchain.passport.chain.observer.common.model;

import java.util.List;

/**
 * StatisticsInfo
 *
 * @author zhengpeng
 */
public class StatisticsInfo {

    private int nodeNumber;

    private long blockNumber;

    private long geneBlockTime;

    private long consensusTime;

    private long totalTransactionNumber;

    private List<ChartPoint<Integer, Integer>> trendList;

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public long getGeneBlockTime() {
        return geneBlockTime;
    }

    public void setGeneBlockTime(long geneBlockTime) {
        this.geneBlockTime = geneBlockTime;
    }

    public long getConsensusTime() {
        return consensusTime;
    }

    public void setConsensusTime(long consensusTime) {
        this.consensusTime = consensusTime;
    }

    public long getTotalTransactionNumber() {
        return totalTransactionNumber;
    }

    public void setTotalTransactionNumber(long totalTransactionNumber) {
        this.totalTransactionNumber = totalTransactionNumber;
    }

    public List<ChartPoint<Integer, Integer>> getTrendList() {
        return trendList;
    }

    public void setTrendList(List<ChartPoint<Integer, Integer>> trendList) {
        this.trendList = trendList;
    }
}
