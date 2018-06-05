package io.wexchain.dcc.service.frontend.service.marketing;

import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.service.frontend.model.request.ApplyRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.model.request.QueryRedeemTokenQualificationRequest;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenQualificationVo;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenVo;

import java.util.List;

public interface RedeemTokenService {

    RedeemTokenVo applyRedeemToken(ApplyRedeemTokenRequest request);

    List<RedeemToken> queryBonus(String address);

    RedeemToken applyBonus(String address, Long redeemTokenId);

}
