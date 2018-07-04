package com.xxy.maple.tllibrary.entity;

/**
 * Created by Gaoguanqi on 2018/4/28.
 */

public class GaspriceNonceEntity {
    private String nonce;
    private GasPriceBean gas_price;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public GasPriceBean getGas_price() {
        return gas_price;
    }

    public void setGas_price(GasPriceBean gas_price) {
        this.gas_price = gas_price;
    }

    public static class GasPriceBean {
        private String min;
        private String max;
        private String step_len;
        private String gas;
        private String gas_auth;
        private String gas_trans;

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getStep_len() {
            return step_len;
        }

        public void setStep_len(String step_len) {
            this.step_len = step_len;
        }

        public String getGas() {
            return gas;
        }

        public void setGas(String gas) {
            this.gas = gas;
        }

        public String getGas_auth() {
            return gas_auth;
        }

        public void setGas_auth(String gas_auth) {
            this.gas_auth = gas_auth;
        }

        public String getGas_trans() {
            return gas_trans;
        }

        public void setGas_trans(String gas_trans) {
            this.gas_trans = gas_trans;
        }

        @Override
        public String toString() {
            return "GasPriceBean{" +
                    "min='" + min + '\'' +
                    ", max='" + max + '\'' +
                    ", step_len='" + step_len + '\'' +
                    ", gas='" + gas + '\'' +
                    ", gas_auth='" + gas_auth + '\'' +
                    ", gas_trans='" + gas_trans + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GaspriceNonceEntity{" +
                "nonce='" + nonce + '\'' +
                ", gas_price=" + gas_price +
                '}';
    }
}
