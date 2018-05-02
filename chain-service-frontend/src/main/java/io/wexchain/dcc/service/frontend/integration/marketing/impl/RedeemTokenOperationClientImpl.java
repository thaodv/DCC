package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.facade.RedeemTokenFacade;
import io.wexchain.dcc.marketing.api.model.IdRestriction;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.RedeemTokenOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RedeemTokenOperationClientImpl
 *
 * @author zhengpeng
 */
@Component
public class RedeemTokenOperationClientImpl implements RedeemTokenOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "redeemTokenFacade")
    private IntegrationProxy<RedeemTokenFacade> redeemTokenFacade;


    @Override
    public ListResultResponse<RedeemToken> queryRedeemToken(QueryRedeemTokenRequest request) {
        return ExecuteTemplate.execute(() ->
                redeemTokenFacade.buildInst().queryRedeemToken(request), logger, "查询领取代币订单", request);
    }

    @Override
    public ListResultResponse<IdRestriction> queryIdRestriction(QueryIdRestrictionRequest request) {
        return ExecuteTemplate.execute(() ->
                redeemTokenFacade.buildInst().queryIdRestriction(request), logger, "查询订单约束", request);
    }
}
