package io.wexchain.android.dcc.fragment.home.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi

/**
 *Created by liuyang on 2018/11/8.
 */
class FindPageVm:ViewModel() {

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    private val token: String by lazy {
        App.get().gardenTokenManager.gardenToken!!
    }

    val balance = MutableLiveData<String>()
            .apply {
                getBalance {
                    this.postValue(it)
                }
            }

    fun getBalance(event: (String) -> Unit) {
        api.balance(token)
                .checkonMain()
                .subscribeBy {
                    event.invoke(it.balance)
                }
    }
}