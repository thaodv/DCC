package io.wexchain.passport.chain.observer.common.request;

import io.wexchain.passport.chain.observer.common.constant.TransactionType;

/**
 * QueryJuzixTransactionRequest
 *
 * @author zhengpeng
 */
public class QueryJuzixTransactionRequest extends PageParam {

    private String blockHash;

    private Long blockNumber;

    private String address;

    private TransactionType transactionType;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
