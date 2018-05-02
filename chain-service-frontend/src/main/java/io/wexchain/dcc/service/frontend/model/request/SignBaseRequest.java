package io.wexchain.dcc.service.frontend.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author zhengpeng
 */
public class SignBaseRequest {

    @NotBlank(message = "address 不能为空")
    private String address;

    @NotBlank(message = "signature 不能为空")
    @JsonIgnore
    private String signature;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
