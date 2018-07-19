package io.wexchain.dcc.service.frontend.ctrlr.marketing;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.MiningRewardRecordVo;
import io.wexchain.dcc.service.frontend.service.marketing.MiningRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * ActivityController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class MiningRewardController extends SecurityBaseController{

    @Autowired
    private MiningRewardService miningRewardService;

    @PostMapping("/secure/marketing/mining/queryRecord")
    public ResultResponse<Pagination<MiningRewardRecordVo>> queryMiningRewardRecordPage(@Valid PageParam pageParam, BindingResult bindingResult) {
        Pagination<MiningRewardRecordVo> miningRewardRecordPagination = miningRewardService.queryMiningRewardRecordPage(pageParam, getMember().getUsername());
        return ResultResponseUtils.successResultResponse(miningRewardRecordPagination);
    }
    @PostMapping("/secure/marketing/mining/queryBonus")
    public ResultResponse<Pagination<EcoBonusVo>> queryBonus(@Valid PageParam pageParam, BindingResult bindingResult) {
        Pagination<EcoBonusVo>  rewardVoPagination = miningRewardService.queryBonus(pageParam,getMember().getUsername());
        return ResultResponseUtils.successResultResponse(rewardVoPagination);
    }

    @PostMapping("/secure/marketing/mining/getTotalBonus")
    public ResultResponse<BigDecimal> getTotalEcoBonus() {
        BigDecimal totalEcoBonus = miningRewardService.getTotalEcoBonus(getMember().getUsername());
        return ResultResponseUtils.successResultResponse(totalEcoBonus);
    }

    @PostMapping("/secure/marketing/mining/queryContributionScore")
    public ResultResponse<BigDecimal> queryMiningContributionScore() {
        BigDecimal score = miningRewardService.queryMiningContributionScore(getMember().getUsername());
        return ResultResponseUtils.successResultResponse(score);
    }

    @PostMapping("/marketing/mining/queryRule")
    public ListResultResponse<EcoBonusRuleVo> queryRewardRule() {
        List<EcoBonusRuleVo> ecoRewardRuleVos = miningRewardService.queryRewardRule();
        return ListResultResponseUtils.successListResultResponse(ecoRewardRuleVos);
    }

    @PostMapping("/secure/marketing/mining/signIn")
    public BaseResponse signIn() {
        miningRewardService.signIn(getMember().getUsername());
        return BaseResponseUtils.successBaseResponse();
    }

}
