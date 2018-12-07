package io.wexchain.android.dcc.modules.cashloan.vm

import io.reactivex.Single
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.TnLoanOrder

/**
 *Created by liuyang on 2018/12/7.
 */
class CashLoanRecordsVm : PagedVm<TnLoanOrder>() {

    override fun loadPage(page: Int): Single<PagedList<TnLoanOrder>> {
        return ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.queryOrderPage(it, page)
                }
    }

}