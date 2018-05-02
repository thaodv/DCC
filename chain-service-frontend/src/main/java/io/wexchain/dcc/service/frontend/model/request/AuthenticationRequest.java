package io.wexchain.dcc.service.frontend.model.request;

import javax.validation.constraints.NotBlank;

/**
 * AuthenticationRequest
 *
 * @author zhengpeng
 */
public class AuthenticationRequest extends SignBaseRequest{

    @NotBlank(message = "nonce 不能为空")
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
