package io.wexchain.dcc.service.frontend.ctrlr.marketing;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.marketing.api.model.RewardRound;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.YesterdayEcoBonusVo;
import io.wexchain.dcc.service.frontend.service.marketing.impl.EcoServiceImpl;
import io.wexchain.dcc.service.frontend.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * ActivityController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class EcoController extends SecurityBaseController{

    @Autowired
    private EcoServiceImpl ecoService;

    @PostMapping("/secure/marketing/eco/queryBonus")
    public ResultResponse<Pagination<EcoBonusVo>> queryEcoBonus(@Valid PageParam pageParam, BindingResult bindingResult) {
        Pagination<EcoBonusVo>  rewardVoPagination = ecoService.queryBonus(pageParam,getMember().getUsername());
        return ResultResponseUtils.successResultResponse(rewardVoPagination);
    }

    @PostMapping("/secure/marketing/eco/getTotalBonus")
    public ResultResponse<BigDecimal> getTotalEcoBonus() {
        BigDecimal totalEcoBonus = ecoService.getTotalEcoBonus(getMember().getUsername());
        return ResultResponseUtils.successResultResponse(totalEcoBonus);
    }

    @PostMapping("/secure/marketing/eco/getYesterdayBonus")
    public ResultResponse<YesterdayEcoBonusVo> getYesterdayEcoBonus() {
        YesterdayEcoBonusVo yesterdayBonus = ecoService.getYesterdayBonus(getMember().getUsername());
        return ResultResponseUtils.successResultResponse(yesterdayBonus);
    }

    @PostMapping("/marketing/eco/queryBonusRule")
    public ListResultResponse<EcoBonusRuleVo> queryBonusRule() {
        List<EcoBonusRuleVo> ecoBonusRuleVos = ecoService.queryBonusRule();
        return ListResultResponseUtils.successListResultResponse(ecoBonusRuleVos);
    }

    @PostMapping("/marketing/eco/createRewardRound")
    public BaseResponse createRewardRound(HttpServletRequest request, @DateTimeFormat(pattern = DateUtil.shortFormat)Date bonusDay) {
        String header = request.getHeader("x-advance");
        if(!"wjLFVSDjb3soKfaj".equals(header)){
            return BaseResponseUtils.codeBaseResponse(SystemCode.SUCCESS,"404","接口不存在");
        }
        RewardRound rewardRounds = ecoService.createRewardRound(bonusDay);
        return ResultResponseUtils.successResultResponse(rewardRounds);
    }


}
