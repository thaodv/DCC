package io.wexchain.android.dcc.modules.trans.vm

import io.reactivex.Single
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dccchainservice.domain.AccrossTransRecord
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.util.DateUtil

class AcrossTransRecordsVm : PagedVm<AccrossTransRecord>() {

    var startTime: String = DateUtil.getCurrentMonday()
    var endTime: String = DateUtil.getCurrentSunday()


    override fun loadPage(page: Int): Single<PagedList<AccrossTransRecord>> {
        return ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getChainExchangeList(it, startTime, endTime, page.toLong(), PAGE_SIZE.toLong())
                }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
