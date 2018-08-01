package io.wexchain.android.dcc.vm

import io.reactivex.Single
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dccchainservice.domain.LoanRecordSummary
import io.wexchain.dccchainservice.domain.PagedList

class LoanRecordsVm : PagedVm<LoanRecordSummary>() {
    override fun loadPage(page: Int): Single<PagedList<LoanRecordSummary>> {
        return ScfOperations
            .withScfTokenInCurrentPassport {
                App.get().scfApi.queryOrderPage(it, page.toLong(), PAGE_SIZE.toLong())
            }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
