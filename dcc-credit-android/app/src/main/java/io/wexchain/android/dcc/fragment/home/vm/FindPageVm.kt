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
    val redPacket = MutableLiveData<RedPacketActivityBean>()
    val time = ObservableField<String>()

    val redpacket1 = SingleLiveEvent<Void>()
    val redpacket2 = SingleLiveEvent<Pair<Long, Long>>()

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

    fun redpacketCall() {
        redPacket.value?.let {
            if (it.status == RedPacketActivityBean.Status.STARTED || it.status == RedPacketActivityBean.Status.ENDED) {
                redpacket1.call()
            } else {
                redpacket2.postValue(it.from to it.to)
            }
        }
    }

    private fun getRedPacket() {
        GardenOperations
                .refreshToken {
                    api.getRedPacketActivity(it).check()
                }
                .doMain()
                .subscribeBy {
                    redPacket.postValue(it)
                    time.set("活动时间 " + DateUtil.getStringTime(it.from, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(it.to, "yyyy.MM.dd"))
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
        getRedPacket()
    }
}