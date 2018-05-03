package io.wexchain.dcc.service.frontend.service.dcc.loan;

import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationPageRequest;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface LoanService {

    Pagination queryOrders(AuthenticationPageRequest request);
}
