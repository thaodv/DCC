package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.model.IdRestriction;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;

public interface RedeemTokenOperationClient {

    /**
     * 查询领取代币资格
     */
    ListResultResponse<RedeemToken> queryRedeemToken(QueryRedeemTokenRequest request);


    /**
     * 查询领取代币资格
     */
    ListResultResponse<IdRestriction> queryIdRestriction(QueryIdRestrictionRequest request);


}
