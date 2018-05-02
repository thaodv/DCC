package io.wexchain.dcc.service.frontend.model.vo;

/**
 * BankInfoVo
 *
 * @author zhengpeng
 */
public class BankInfoVo {

    private String bankName;

    private String bankCode;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public BankInfoVo() {
    }

    public BankInfoVo(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }
}
