package io.wexchain.dcc.service.frontend.ctrlr.marketing;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.ApplyRedeemTokenRequest;
import io.wexchain.dcc.service.frontend.model.request.QueryActivityVoRequest;
import io.wexchain.dcc.service.frontend.model.request.QueryRedeemTokenQualificationRequest;
import io.wexchain.dcc.service.frontend.model.vo.ActivityVo;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenQualificationVo;
import io.wexchain.dcc.service.frontend.service.marketing.ActivityService;
import io.wexchain.dcc.service.frontend.service.marketing.RedeemTokenService;
import io.wexchain.dcc.service.frontend.service.marketing.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ActivityController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class ActivityController extends SecurityBaseController{

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private RedeemTokenService redeemTokenService;


    @GetMapping("/marketing/queryActivity")
    public ResultResponse<List<ActivityVo>> queryActivityVo(QueryActivityVoRequest request) {
        return ResultResponseUtils.successResultResponse(activityService.queryActivity(request));
    }

    @GetMapping("/marketing/queryScenario")
    public ResultResponse<List<RedeemTokenQualificationVo>> queryScenarioVo(QueryRedeemTokenQualificationRequest request) {
        return ResultResponseUtils.successResultResponse(
                scenarioService.queryRedeemTokenQualification(request));
    }

    @PostMapping("/marketing/applyRedeemToken")
    public BaseResponse redeemToken(ApplyRedeemTokenRequest request) {
        redeemTokenService.applyRedeemToken(request);
        return BaseResponseUtils.successBaseResponse();
    }

    @PostMapping("/secure/marketing/queryBonus")
    public ListResultResponse<RedeemToken> queryBonus() {
        List<RedeemToken> list = redeemTokenService.queryBonus(getMember().getUsername());
        return ListResultResponseUtils.successListResultResponse(list);
    }

    @PostMapping("/secure/marketing/applyBonus")
    public ResultResponse<RedeemToken> applyBonus(@NotNull(message = "红包订单号不能为空") Long redeemTokenId) {
        RedeemToken redeemToken = redeemTokenService.applyBonus(getMember().getUsername(), redeemTokenId);
        return ResultResponseUtils.successResultResponse(redeemToken);
    }




}
