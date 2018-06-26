package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/27.
 */

/**
 * 归还 接收H5的数据
 */
public class TransferRevertData {


    /**
     * type =4
     order_no
     sign_type      1不需要签名2需要签名
     loan_address   借款人地址
     token_address  token合约地址
     new_address    新合约地址
     repay_amount   要还的数量
     eth_service_fee 手续费
     eth_total_fee
     */

    private String type;
    private String order_no;
    private String sign_type;
    private String loan_address;
    private String token_address;
    private String new_address;
    private String amount;
    private String eth_service_fee;
    private String eth_total_fee;

    private String transaction_fee;//服务费
    private String transaction_amount;//交易额度


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getLoan_address() {
        return loan_address;
    }

    public void setLoan_address(String loan_address) {
        this.loan_address = loan_address;
    }

    public String getToken_address() {
        return token_address;
    }

    public void setToken_address(String token_address) {
        this.token_address = token_address;
    }

    public String getNew_address() {
        return new_address;
    }

    public void setNew_address(String new_address) {
        this.new_address = new_address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEth_service_fee() {
        return eth_service_fee;
    }

    public void setEth_service_fee(String eth_service_fee) {
        this.eth_service_fee = eth_service_fee;
    }

    public String getEth_total_fee() {
        return eth_total_fee;
    }

    public void setEth_total_fee(String eth_total_fee) {
        this.eth_total_fee = eth_total_fee;
    }

    public String getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(String transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    @Override
    public String toString() {
        return "TransferRevertData{" +
                "type='" + type + '\'' +
                ", order_no='" + order_no + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", loan_address='" + loan_address + '\'' +
                ", token_address='" + token_address + '\'' +
                ", new_address='" + new_address + '\'' +
                ", amount='" + amount + '\'' +
                ", eth_service_fee='" + eth_service_fee + '\'' +
                ", eth_total_fee='" + eth_total_fee + '\'' +
                ", transaction_fee='" + transaction_fee + '\'' +
                ", transaction_amount='" + transaction_amount + '\'' +
                '}';
    }
}
