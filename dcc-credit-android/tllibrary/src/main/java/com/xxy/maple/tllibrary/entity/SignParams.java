package com.xxy.maple.tllibrary.entity;

import org.web3j.abi.datatypes.Address;

import java.math.BigInteger;

/**
 * Created by Gaoguanqi on 2018/5/30.
 */

public class SignParams {
    private Address from;
    private Address to;
    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private BigInteger value;
    private String dataHex;

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getDataHex() {
        return dataHex;
    }

    public void setDataHex(String dataHex) {
        this.dataHex = dataHex;
    }
}
