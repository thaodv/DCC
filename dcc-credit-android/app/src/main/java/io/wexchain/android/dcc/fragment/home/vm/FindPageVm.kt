package io.wexchain.android.dcc.fragment.home.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.redpacket.RedPacketActivityBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

/**
 *Created by liuyang on 2018/11/8.
 */
class FindPageVm : ViewModel() {

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    val balance = MutableLiveData<String>()
    val lastDuel = MutableLiveData<ChangeOrder>()
    val queryFlower = MutableLiveData<Boolean>()

    private fun getBalance() {
        GardenOperations
                .refreshToken {
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
                .refreshToken {
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
