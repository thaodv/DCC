package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/27.
 */

/**
 * 出借 接收H5的数据
 */
public class TransferLendData {


/**
 * type =3
 order_no
 sign_type      1不需要签名2需要签名
 lender_address 出借人地址
 new_address     新合约地址
 token_address  出借币种的token地址
 loan_amount    出借数量
 eth_service_fee 出借服务费
 loan_address 借款人地址；
 */

    /**
     * {
     "order_no": "100011805056948930852",
     "type": 3,
     "lender_address": "0xf3a0631395372e5e14c2d20e332fb79e42fd956e",
     "new_address": "0x4fe58a774401f606e41ff7aab2bcc607aa6aee82",
     "sign_type": 2,
     "loan_amount": "55000000000000000000",
     "token_address": "0x3fcc1803480e879fda49c5e79a56f4c48e109c2d",
     "eth_service_fee": "1171352600000000"
     }
     */

    private String order_no;
    private String type;
    private String lender_address;

    private String new_address;
    private String sign_type;
    private String amount;
    private String token_address;
    private String eth_service_fee;
    private String loan_address;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getLoan_address() {
        return loan_address;
    }

    public void setLoan_address(String loan_address) {
        this.loan_address = loan_address;
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
        return "TransferLendData{" +
                "order_no='" + order_no + '\'' +
                ", type='" + type + '\'' +
                ", lender_address='" + lender_address + '\'' +
                ", new_address='" + new_address + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", amount='" + amount + '\'' +
                ", token_address='" + token_address + '\'' +
                ", eth_service_fee='" + eth_service_fee + '\'' +
                ", loan_address='" + loan_address + '\'' +
                ", eth_total_fee='" + eth_total_fee + '\'' +
                ", transaction_fee='" + transaction_fee + '\'' +
                ", transaction_amount='" + transaction_amount + '\'' +
                '}';
    }
}
