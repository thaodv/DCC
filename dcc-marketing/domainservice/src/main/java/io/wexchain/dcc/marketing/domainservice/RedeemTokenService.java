package io.wexchain.dcc.marketing.domainservice;

import java.util.List;

import io.wexchain.dcc.marketing.api.constant.RedeemTokenQualification;
import io.wexchain.dcc.marketing.api.model.request.ApplyBonusRequest;
import io.wexchain.dcc.marketing.api.model.request.GetRedeemTokenQualificationRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.marketing.api.model.request.RedeemTokenRequest;
import io.wexchain.dcc.marketing.domain.IdRestriction;
import io.wexchain.dcc.marketing.domain.RedeemToken;

public interface RedeemTokenService {

    RedeemTokenQualification getRedeemTokenQualification(
            GetRedeemTokenQualificationRequest request);

    RedeemToken redeemToken(RedeemTokenRequest request);

    List<RedeemToken> queryRedeemToken(QueryRedeemTokenRequest request);

    List<IdRestriction> queryIdRestriction(QueryIdRestrictionRequest request);

    RedeemToken createBonus(RedeemTokenRequest request);

    RedeemToken applyBonus(ApplyBonusRequest request);
}
