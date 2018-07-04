package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/27.
 */

/**
 * 取消 接收H5的数据
 */
public class TransferCancleData {


    /**
     * loan_address : 0x80ea47ca1c401721997f20993b4d192d161df36f
     * order_no : 100011805112953670579
     * type : 6
     * sign_type : 1
     */

    private String loan_address;
    private String order_no;
    private String type;
    private String sign_type;
    private String new_address;

    private String transaction_fee;//服务费
    private String transaction_amount;//交易额度


    public String getLoan_address() {
        return loan_address;
    }

    public void setLoan_address(String loan_address) {
        this.loan_address = loan_address;
    }

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

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getNew_address() {
        return new_address;
    }

    public void setNew_address(String new_address) {
        this.new_address = new_address;
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
        return "TransferCancleData{" +
                "loan_address='" + loan_address + '\'' +
                ", order_no='" + order_no + '\'' +
                ", type='" + type + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", new_address='" + new_address + '\'' +
                ", transaction_fee='" + transaction_fee + '\'' +
                ", transaction_amount='" + transaction_amount + '\'' +
                '}';
    }
}
