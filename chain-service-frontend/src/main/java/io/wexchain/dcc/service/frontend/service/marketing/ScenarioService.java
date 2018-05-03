package io.wexchain.dcc.service.frontend.service.marketing;

import io.wexchain.dcc.service.frontend.model.request.QueryRedeemTokenQualificationRequest;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenQualificationVo;

import java.util.List;

public interface ScenarioService {

    List<RedeemTokenQualificationVo> queryRedeemTokenQualification(QueryRedeemTokenQualificationRequest request);

}
