package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenQualification;
import io.wexchain.dcc.marketing.api.facade.RedeemTokenFacade;
import io.wexchain.dcc.marketing.api.model.IdRestriction;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.request.GetRedeemTokenQualificationRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.marketing.api.model.request.RedeemTokenRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domainservice.RedeemTokenService;
import io.wexchain.dcc.marketing.ext.service.helper.IdRestrictionResponseHelper;
import io.wexchain.dcc.marketing.ext.service.helper.RedeemTokenResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * RedeemTokenFacadeImpl
 *
 * @author zhengpeng
 */
@Component("redeemTokenFacade")
@Validated
public class RedeemTokenFacadeImpl implements RedeemTokenFacade {

    @Autowired
    private RedeemTokenService redeemTokenService;

    @Autowired
    private RedeemTokenResponseHelper redeemTokenResponseHelper;

    @Autowired
    private IdRestrictionResponseHelper idRestrictionResponseHelper;

    @Override
    public ListResultResponse<RedeemToken> queryRedeemToken(@NotNull @Valid QueryRedeemTokenRequest request) {
        try {
            List<io.wexchain.dcc.marketing.domain.RedeemToken > result = redeemTokenService.queryRedeemToken(request);
            return redeemTokenResponseHelper.returnListSuccess(result);
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }

    @Override
    public ListResultResponse<IdRestriction> queryIdRestriction(@NotNull @Valid QueryIdRestrictionRequest request) {
        try {
            List<io.wexchain.dcc.marketing.domain.IdRestriction> result = redeemTokenService.queryIdRestriction(request);
            return idRestrictionResponseHelper.returnListSuccess(result);
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }

    @Override
    public ResultResponse<RedeemTokenQualification> getRedeemTokenQualification(
            @NotNull @Valid GetRedeemTokenQualificationRequest request) {
        try {
            RedeemTokenQualification result = redeemTokenService.getRedeemTokenQualification(request);
            return ResultResponseUtils.successResultResponse(result);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @Override
    public ResultResponse<RedeemToken> applyRedeemToken(@NotNull @Valid RedeemTokenRequest request) {
        try {
            io.wexchain.dcc.marketing.domain.RedeemToken result = redeemTokenService.redeemToken(request);
            return redeemTokenResponseHelper.returnSuccess(result);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
