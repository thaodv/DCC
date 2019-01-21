package io.wexchain.android.dcc.fragment.home.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.CricketCount
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
    val cricketCount = MutableLiveData<String>()
    val cricketTime = MutableLiveData<String>()

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

    private fun getCricketCount() {
        GardenOperations
                .refreshToken {
                    api.getCricketPlayer(it)
                            .map {
                                if (it.isSuccess) {
                                    it.result ?: CricketCount.empty()
                                } else {
                                    throw it.asError()
                                }
                            }
                }
                .doMain()
                .subscribeBy {
                    if (it == CricketCount.empty()) {
                        cricketCount.postValue("家有蟋蟀初养成，等待主人来领养！")
                    } else {
                        cricketCount.postValue(/*unitFormat(it.coin)*/"主人，您有离线收益未收取哦")
                        val time = if (it.offlineDurationUnit == CricketCount.Unit.MINUTES) {
                            "${it.offlineDuration} 分钟前"
                        } else {
                            "${it.offlineDuration} 小时前"
                        }
                        cricketTime.postValue(time)
                    }
                }

    }

    private fun unitFormat(data: String): String {
        return when {
            data.length <= 4 -> {
                data
            }
            data.length <= 8 -> {
                "${data.substring(0, 4)} 万"
            }
            data.length <= 12 -> {
                "${data.substring(0, 4)} 亿"
            }
            data.length <= 16 -> {
                "${data.substring(0, 4)} 兆"
            }
            data.length <= 20 -> {
                "${data.substring(0, 4)} 京"
            }
            data.length <= 24 -> {
                "${data.substring(0, 4)} 垓"
            }
            data.length <= 28 -> {
                "${data.substring(0, 4)} 秭"
            }
            data.length <= 32 -> {
                "${data.substring(0, 4)} 穰"
            }
            data.length <= 36 -> {
                "${data.substring(0, 4)} 沟"
            }
            data.length <= 40 -> {
                "${data.substring(0, 4)} 涧"
            }
            data.length <= 44 -> {
                "${data.substring(0, 4)} 正"
            }
            else -> {
                "${data.substring(0, 4)} *"
            }
        }
    }

    fun refresh() {
        getBalance()
        getLastDuel()
        queryFlower()
        getCricketCount()
    }
}
