package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.BonusFacade;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.ApplyBonusRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.BonusOperationClient;
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
public class BonusOperationClientImpl implements BonusOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "bonusFacade")
    private IntegrationProxy<BonusFacade> bonusFacade;

    @Override
    public ResultResponse<RedeemToken> applyBonus(ApplyBonusRequest request) {
        return ExecuteTemplate.execute(() ->
                bonusFacade.buildInst().applyBonus(request), logger, "领取红包", request);
    }
}
