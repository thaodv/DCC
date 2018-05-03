package io.wexchain.dcc.service.frontend.ctrlr.dcc.loan;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.ctrlr.SecurityBaseController;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.service.wexyun.LoanCreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * LoanProductController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/secure/loan/credit")
@Validated
public class LoanCreditController extends SecurityBaseController {

    @Autowired
    private LoanCreditService loanCreditService;

    @PostMapping("/apply")
    public ResultResponse<Long> getList(@Valid LoanCreditApplyRequest loanCreditApplyRequest) {
        Long applyId = loanCreditService.apply(loanCreditApplyRequest,getMemberId());
        return ResultResponseUtils.successResultResponse(applyId);
    }

}
