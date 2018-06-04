package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/27.
 */

/**
 * 抵押 接收H5的数据
 */
public class TransferMortgageData {


    /**
     * order_no : 100011805038922980571
     * type : 2
     * loan_address : 0x749b497bf61f13cbb56015d6642f98a27316a18f
     * new_address : null
     * sign_type : 2
     * token_address : ""
     * eth_service_fee : 0.001
     */



    private String order_no;
    private String type;
    private String loan_address;
    private String new_address;
    private String sign_type;
    private String token_address;
    private String eth_service_fee;
    private String amount;

    private String eth_total_fee;
    private String transaction_fee;//服务费
    private String transaction_amount;//交易额度




    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLoan_address() {
        return loan_address;
    }

    public void setLoan_address(String loan_address) {
        this.loan_address = loan_address;
    }

    public String getNew_address() {
        return new_address;
    }

    public void setNew_address(String new_address) {
        this.new_address = new_address;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getToken_address() {
        return token_address;
    }

    public void setToken_address(String token_address) {
        this.token_address = token_address;
    }

    public String getEth_service_fee() {
        return eth_service_fee;
    }

    public void setEth_service_fee(String eth_service_fee) {
        this.eth_service_fee = eth_service_fee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
        return "TransferMortgageData{" +
                "order_no='" + order_no + '\'' +
                ", type='" + type + '\'' +
                ", loan_address='" + loan_address + '\'' +
                ", new_address='" + new_address + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", token_address='" + token_address + '\'' +
                ", eth_service_fee='" + eth_service_fee + '\'' +
                ", amount='" + amount + '\'' +
                ", eth_total_fee='" + eth_total_fee + '\'' +
                ", transaction_fee='" + transaction_fee + '\'' +
                ", transaction_amount='" + transaction_amount + '\'' +
                '}';
    }
}
