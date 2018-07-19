package io.wexchain.dcc.service.frontend.ctrlr.marketing;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.vo.CandyVo;
import io.wexchain.dcc.service.frontend.service.marketing.CandyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ActivityController
 *
 * @author zhengpeng
 */
@RestController
@Validated
@RequestMapping("/secure/marketing/candy")
public class CandyController extends SecurityBaseController{

    @Resource(name = "candyServiceImpl")
    private CandyService candyService;

    @PostMapping("/pick")
    public ResultResponse<CandyVo> pick(@NotNull(message = "糖果单号不能为空") Long candyId) {
        CandyVo candyVo = candyService.pickCandy(getMember().getUsername(), candyId);
        return ResultResponseUtils.successResultResponse(candyVo);
    }

    @PostMapping("/queryList")
    public ListResultResponse<CandyVo> queryList(@NotBlank(message = "boxCode不能为空") String boxCode) {
        List<CandyVo> candyVos = candyService.queryCandyList(getMember().getUsername(), boxCode);
        return ListResultResponseUtils.successListResultResponse(candyVos);
    }

}
