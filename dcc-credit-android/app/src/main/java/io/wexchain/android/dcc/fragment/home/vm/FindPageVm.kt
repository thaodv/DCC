package io.wexchain.android.dcc.fragment.home.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.ipfs.utils.doMain

/**
 *Created by liuyang on 2018/11/8.
 */
class FindPageVm : ViewModel() {

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    val balance = MutableLiveData<String>()
            .apply {
                getBalance()
            }

    val lastDuel = MutableLiveData<ChangeOrder>()
            .apply {
                getLastDuel()
            }

    val queryFlower = MutableLiveData<Boolean>()
            .apply {
                queryFlower()
            }


    private fun getBalance() {
        GardenOperations
                .refirshToken {
                    api.balance(it)
                }
                .checkonMain()
                .subscribeBy {
                    balance.postValue(it.balance)
                }
    }

    private fun getLastDuel() {
        api.getLastDuel()
                .checkonMain()
                .subscribeBy {
                    lastDuel.postValue(it)
                }
    }

    private fun queryFlower() {
        GardenOperations
                .refirshToken {
                    api.queryFlower(it, App.get().userInfo!!.member.id)
                            .map {
                                it.isSuccess && it.result != null
                            }
                }
                .doMain()
                .subscribeBy {
                    queryFlower.postValue(it)
                }
    }

    fun refresh() {
        getBalance()
        getLastDuel()
        queryFlower()
    }
}