package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.BonusFacade;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.ApplyBonusRequest;
import io.wexchain.dcc.marketing.api.model.request.RedeemTokenRequest;
import io.wexchain.dcc.marketing.domainservice.RedeemTokenService;
import io.wexchain.dcc.marketing.ext.service.helper.RedeemTokenResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * RedeemTokenFacadeImpl
 *
 * @author zhengpeng
 */
@Component("bonusFacade")
@Validated
public class BonusFacadeImpl implements BonusFacade {

    @Autowired
    private RedeemTokenService redeemTokenService;

    @Autowired
    private RedeemTokenResponseHelper redeemTokenResponseHelper;

    @Override
    public ResultResponse<RedeemToken> createBonus(RedeemTokenRequest request) {
        try {
            io.wexchain.dcc.marketing.domain.RedeemToken result = redeemTokenService.createBonus(request);
            return redeemTokenResponseHelper.returnSuccess(result);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @Override
    public ResultResponse<RedeemToken> applyBonus(ApplyBonusRequest request) {
        try {
            io.wexchain.dcc.marketing.domain.RedeemToken result = redeemTokenService.applyBonus(request);
            return redeemTokenResponseHelper.returnSuccess(result);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
