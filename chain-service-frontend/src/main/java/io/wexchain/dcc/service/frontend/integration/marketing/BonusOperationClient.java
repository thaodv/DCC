package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.ApplyBonusRequest;

public interface BonusOperationClient {

    /**
     * 领取红包
     */
    ResultResponse<RedeemToken> applyBonus(ApplyBonusRequest request);
}
