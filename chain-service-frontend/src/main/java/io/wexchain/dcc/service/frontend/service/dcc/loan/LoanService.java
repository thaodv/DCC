package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.model.request.QueryLoanOrderPageRequest;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface LoanService {

    Pagination queryOrders(QueryLoanOrderPageRequest request, MemberDetails memberDetails);

    LoanOrder getLastOrder(MemberDetails memberDetails);
}
