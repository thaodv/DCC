package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/27.
 */

/**
 * 赔付 接收H5的数据
 */
public class TransferIndemnityData {

    /**
     * type  5
     order_no
     lender_address    出借人地址
     new_address       新合约地址
     sign_type
     */

    private String type;
    private String order_no;
    private String lender_address;
    private String new_address;
    private String sign_type;

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

    public String getLender_address() {
        return lender_address;
    }

    public void setLender_address(String lender_address) {
        this.lender_address = lender_address;
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
        return "TransferIndemnityData{" +
                "type='" + type + '\'' +
                ", order_no='" + order_no + '\'' +
                ", lender_address='" + lender_address + '\'' +
                ", new_address='" + new_address + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", transaction_fee='" + transaction_fee + '\'' +
                ", transaction_amount='" + transaction_amount + '\'' +
                '}';
    }
}
