package io.wexchain.dcc.service.frontend.model.request;

/**
 * QueryRedeemTokenQualificationRequest
 *
 * @author zhengpeng
 */
public class QueryRedeemTokenQualificationRequest {

    private String activityCode;

    private String address;

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
