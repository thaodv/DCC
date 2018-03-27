package io.wexchain.passport.chain.observer.common.model;

import java.math.BigInteger;
import java.util.Date;

/**
 * JuzixBlockVo
 *
 * @author zhengpeng
 */
public class JuzixBlockVo {

    private String hash;
    private String parentHash;
    private Long blockNumber;
    private String nonce;
    private String mixHash;
    private String miner;
    private BigInteger difficulty;
    private BigInteger totalDifficulty;
    private Long blockSize;
    private BigInteger gasLimit;
    private BigInteger gasUsed;
    private Date blockTimestamp;
    private Integer transactionCount;
    private String extraData;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getMixHash() {
        return mixHash;
    }

    public void setMixHash(String mixHash) {
        this.mixHash = mixHash;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public BigInteger getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BigInteger difficulty) {
        this.difficulty = difficulty;
    }

    public BigInteger getTotalDifficulty() {
        return totalDifficulty;
    }

    public void setTotalDifficulty(BigInteger totalDifficulty) {
        this.totalDifficulty = totalDifficulty;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Date getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Date blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
