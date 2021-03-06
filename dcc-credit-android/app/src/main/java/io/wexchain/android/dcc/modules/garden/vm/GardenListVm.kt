package io.wexchain.android.dcc.modules.garden.vm

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.ipfs.utils.doMain

/**
 *Created by liuyang on 2018/11/7.
 */
class GardenListVm : PagedVm<ChangeOrder>() {

    override fun loadPage(page: Int): Single<PagedList<ChangeOrder>> {
        return GardenOperations
                .refreshToken {
                    api.changeOrder(it, page, PAGE_SIZE).check()
                }
    }

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    val balance = MutableLiveData<String>()
            .apply {
                getBalance {
                    this.postValue(it)
                }
            }

    fun getBalance(event: (String) -> Unit) {
        GardenOperations
                .refreshToken {
                    api.balance(it).check()
                }
                .doMain()
                .subscribeBy {
                    event.invoke(it.balance)
                }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }

}