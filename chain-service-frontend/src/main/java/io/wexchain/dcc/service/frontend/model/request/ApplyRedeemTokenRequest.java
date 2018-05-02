package io.wexchain.dcc.service.frontend.model.request;

import javax.validation.constraints.NotBlank;

/**
 * ApplyRedeemTokenRequest
 *
 * @author zhengpeng
 */
public class ApplyRedeemTokenRequest {

    @NotBlank
    private String scenarioCode;

    @NotBlank
    private String address;

    public String getScenarioCode() {
        return scenarioCode;
    }

    public void setScenarioCode(String scenarioCode) {
        this.scenarioCode = scenarioCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
