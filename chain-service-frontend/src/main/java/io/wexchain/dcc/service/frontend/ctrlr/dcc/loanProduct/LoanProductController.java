package io.wexchain.dcc.service.frontend.ctrlr.dcc.loanProduct;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.ctrlr.BaseController;
import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;
import io.wexchain.dcc.service.frontend.service.dcc.loanProduct.LoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * LoanProductController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/loan/product")
@Validated
public class LoanProductController extends BaseController {

    @Autowired
    private LoanProductService loanProductService;

    @PostMapping("/queryByLenderCode")
    public ListResultResponse<LoanProductVo> getList(String lenderCode) {
        List<LoanProductVo> loanProductVoList = loanProductService.getLoanProductVoList(lenderCode);
        return ListResultResponseUtils.successListResultResponse(loanProductVoList);
    }
    @PostMapping("/getById")
    public ResultResponse<LoanProductVo> getList(@NotNull(message = "id不能为空") Long id) {
        LoanProductVo loanProductVo = loanProductService.getLoanProductVo(id);
        return ResultResponseUtils.successResultResponse(loanProductVo);
    }


}
