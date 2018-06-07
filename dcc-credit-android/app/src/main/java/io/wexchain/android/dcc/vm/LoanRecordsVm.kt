package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
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