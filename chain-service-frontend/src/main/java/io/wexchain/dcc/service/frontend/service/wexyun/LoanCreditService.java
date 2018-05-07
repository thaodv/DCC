package io.wexchain.dcc.service.frontend.service.wexyun;

import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface LoanCreditService {

    Long apply(LoanCreditApplyRequest credit2ApplyAddRequest, MemberDetails memberDetails);
}
