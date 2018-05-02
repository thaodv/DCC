package io.wexchain.dcc.service.frontend.ctrlr.dcc.loan;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.ctrlr.AuthenticationController;
import io.wexchain.dcc.service.frontend.ctrlr.BaseController;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationPageRequest;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.service.dcc.loan.LoanService;
import io.wexchain.dcc.service.frontend.service.wexyun.LoanCreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * LoanProductController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/loan")
@Validated
public class LoanController extends BaseController {

    @Autowired
    private AuthenticationController authenticationController;

    @Resource(name = "dccLoanService")
    private LoanService dccLoanService;

    @PostMapping("/queryOrders")
    public ResultResponse queryOrders(@Valid AuthenticationPageRequest pageRequest) {
        authenticationController.validate(pageRequest);
        Pagination pagination = dccLoanService.queryOrders(pageRequest);
        return ResultResponseUtils.successResultResponse(pagination);
    }

}
