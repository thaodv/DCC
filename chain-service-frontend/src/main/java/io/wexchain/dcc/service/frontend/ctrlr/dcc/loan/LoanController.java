package io.wexchain.dcc.service.frontend.ctrlr.dcc.loan;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.ctrlr.AuthenticationController;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.QueryLoanOrderPageRequest;
import io.wexchain.dcc.service.frontend.service.dcc.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * LoanController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/secure/loan")
@Validated
public class LoanController extends SecurityBaseController {

    @Resource(name = "dccLoanService")
    private LoanService dccLoanService;

    @PostMapping("/queryOrders")
    public ResultResponse queryOrders(@Valid QueryLoanOrderPageRequest pageRequest) {
        Pagination pagination = dccLoanService.queryOrders(pageRequest,getMember());
        return ResultResponseUtils.successResultResponse(pagination);
    }

    @PostMapping("/getLastOrder")
    public ResultResponse<LoanOrder> getLastOrder() {
        LoanOrder lastOrder = dccLoanService.getLastOrder(getMember());
        return ResultResponseUtils.successResultResponse(lastOrder);
    }

}
