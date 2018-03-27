package io.wexchain.passport.chain.observer.common.model;

import io.wexchain.passport.chain.observer.common.constant.AddressType;
import io.wexchain.passport.chain.observer.common.constant.JuzixTxStatus;

import java.math.BigInteger;
import java.util.Date;

/**
 * JuzixBlockVo
 *
 * @author zhengpeng
 */
public class JuzixTransactionVo {

    private String hash;
    private String blockHash;
    private Long blockNumber;
    private String nonce;
    private Integer transactionIndex;
    private String fromAddress;
    private AddressType fromType;
    private AddressType toType;
    private String toAddress;
    private String value;
    private String gasPrice;
    private String gas;
    private Date blockTimestamp;
    private String inputData;
    private JuzixTxStatus status;
    private String dccValue;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Integer getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(Integer transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public AddressType getFromType() {
        return fromType;
    }

    public void setFromType(AddressType fromType) {
        this.fromType = fromType;
    }

    public AddressType getToType() {
        return toType;
    }

    public void setToType(AddressType toType) {
        this.toType = toType;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public Date getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Date blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public JuzixTxStatus getStatus() {
        return status;
    }

    public void setStatus(JuzixTxStatus status) {
        this.status = status;
    }

    public String getDccValue() {
        return dccValue;
    }

    public void setDccValue(String dccValue) {
        this.dccValue = dccValue;
    }
}
