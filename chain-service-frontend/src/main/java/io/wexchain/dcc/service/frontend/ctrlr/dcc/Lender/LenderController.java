package io.wexchain.dcc.service.frontend.ctrlr.dcc.Lender;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.model.Lender;
import io.wexchain.dcc.service.frontend.ctrlr.BaseController;
import io.wexchain.dcc.service.frontend.service.dcc.lender.LenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


/**
 * LenderController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/lender")
@Validated
public class LenderController extends BaseController {

    @Autowired
    private LenderService lenderService;

    @GetMapping("/get")
    public ResultResponse<Lender> get(@NotBlank(message = "code不能为空") String code) {
        Lender lender = lenderService.getLender(code);
        return ResultResponseUtils.successResultResponse(lender);
    }

    @GetMapping("/getDefault")
    public ResultResponse<Lender> getDefault() {
        Lender lender = lenderService.getDefaultLender();
        return ResultResponseUtils.successResultResponse(lender);
    }

    @GetMapping("/query")
    public ListResultResponse<Lender> getList() {
        return ListResultResponseUtils.successListResultResponse(lenderService.getLenderList());
    }

}
