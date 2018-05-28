package io.wexchain.cryptoasset.loan.service.function.wexyun.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wexyun.open.api.constants.ApiServicesNames;
import com.wexyun.open.api.request.BaseRequest;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;

/**
 * <p>
 * 信贷2.0进件详情查询请求参数
 * </p>
 * 
 * @author xuegang
 * @version $Id: Credit2ApplyGetRequest.java, v 0.1 2017年5月22日 下午5:11:28 xuegang Exp $
 */
public class Credit2ApplyGetRequest extends BaseRequest {
    /**
     * 信贷进件id
     */
    private String applyId;

    /**
     * 信贷进件发放的兑换码（与redeemCode任选其一传值，不可两者都传）
     */
    private String redeemCode;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    @Override
    public String getService() {
        return ApiServicesNames.CREDIT2_APPLY_GET;
    }

    @Override
    public TypeReference<? extends BaseResponse> getTypeReference() {
        return new TypeReference<QueryResponse4Single<Credit2Apply>>() {
        };
    }
}
