package io.wexchain.passport.chain.observer.common.request;

import io.wexchain.passport.chain.observer.common.constant.TransactionType;

/**
 * QueryJuzixTransactionRequest
 *
 * @author zhengpeng
 */
public class QueryJuzixTokenTransferRequest extends PageParam {

    private String contractAddress;

    private String address;

    private String transactionHash;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
