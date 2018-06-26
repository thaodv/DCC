package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/5/29.
 */

public class TransferCreateData {

    private String order_no;
    private String transaction_amount;
    private String transaction_fee;
    private String master_contract_address;
    private String loan_address;
    private String type;
    private String nonce;
    private ParamBean param;

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(String transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getMaster_contract_address() {
        return master_contract_address;
    }

    public void setMaster_contract_address(String master_contract_address) {
        this.master_contract_address = master_contract_address;
    }

    public String getLoan_address() {
        return loan_address;
    }

    public void setLoan_address(String loan_address) {
        this.loan_address = loan_address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public static class ParamBean {

        private String pledge_token_address;
        private String pledge_amount;
        private String loan_token_address;
        private String loan_amount;
        private String platform_accounts_address;
        private String platform_account_rate;
        private String wallet_accounts_address;
        private String pledge_service_fee;
        private String lender_service_fee;
        private String repay_amount;
        private String repay_service_fee;
        private String compensate_service_fee;
        private String compensate_amount;

        public String getPledge_token_address() {
            return pledge_token_address;
        }

        public void setPledge_token_address(String pledge_token_address) {
            this.pledge_token_address = pledge_token_address;
        }

        public String getPledge_amount() {
            return pledge_amount;
        }

        public void setPledge_amount(String pledge_amount) {
            this.pledge_amount = pledge_amount;
        }

        public String getLoan_token_address() {
            return loan_token_address;
        }

        public void setLoan_token_address(String loan_token_address) {
            this.loan_token_address = loan_token_address;
        }

        public String getLoan_amount() {
            return loan_amount;
        }

        public void setLoan_amount(String loan_amount) {
            this.loan_amount = loan_amount;
        }

        public String getPlatform_accounts_address() {
            return platform_accounts_address;
        }

        public void setPlatform_accounts_address(String platform_accounts_address) {
            this.platform_accounts_address = platform_accounts_address;
        }

        public String getPlatform_account_rate() {
            return platform_account_rate;
        }

        public void setPlatform_account_rate(String platform_account_rate) {
            this.platform_account_rate = platform_account_rate;
        }

        public String getWallet_accounts_address() {
            return wallet_accounts_address;
        }

        public void setWallet_accounts_address(String wallet_accounts_address) {
            this.wallet_accounts_address = wallet_accounts_address;
        }

        public String getPledge_service_fee() {
            return pledge_service_fee;
        }

        public void setPledge_service_fee(String pledge_service_fee) {
            this.pledge_service_fee = pledge_service_fee;
        }

        public String getLender_service_fee() {
            return lender_service_fee;
        }

        public void setLender_service_fee(String lender_service_fee) {
            this.lender_service_fee = lender_service_fee;
        }

        public String getRepay_amount() {
            return repay_amount;
        }

        public void setRepay_amount(String repay_amount) {
            this.repay_amount = repay_amount;
        }

        public String getRepay_service_fee() {
            return repay_service_fee;
        }

        public void setRepay_service_fee(String repay_service_fee) {
            this.repay_service_fee = repay_service_fee;
        }

        public String getCompensate_service_fee() {
            return compensate_service_fee;
        }

        public void setCompensate_service_fee(String compensate_service_fee) {
            this.compensate_service_fee = compensate_service_fee;
        }

        public String getCompensate_amount() {
            return compensate_amount;
        }

        public void setCompensate_amount(String compensate_amount) {
            this.compensate_amount = compensate_amount;
        }
    }
}
