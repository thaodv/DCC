package io.wexchain.android.dcc.modules.garden.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.TaskList
import io.wexchain.dccchainservice.domain.WeekRecord
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/11/7.
 */
class GardenTaskVm : ViewModel() {

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    private val token: String by lazy {
        App.get().gardenTokenManager.gardenToken!!
    }

    val taskList = MutableLiveData<List<TaskList>>()
            .apply {
                api.getTaskList(token)
                        .checkonMain()
                        .subscribeBy {
                            this.postValue(it)
                        }
            }

    val currentWeekRecord = MutableLiveData<List<WeekRecord>>()
            .apply {
                getWeekRecordList {
                    this.postValue(it)
                }
            }


    val isapply = MutableLiveData<Boolean>()
            .apply {
                getSignStatus {
                    this.postValue(it)
                }
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

    fun getSignStatus(event: (Boolean) -> Unit) {
        api.queryTodayRecord(token)
                .map {
                    it.isSuccess && it.result != null
                }
                .io_main()
                .subscribeBy {
                    event.invoke(it)
                }
    }

    fun getWeekRecordList(event: (List<WeekRecord>) -> Unit) {
        api.currentWeekRecord(token)
                .checkonMain()
                .subscribeBy {
                    event.invoke(it)
                }
    }

    fun withSign() {
        if (isapply.value!!) {
            return
        }
        api.apply(token)
                .checkonMain()
                .subscribeBy {
                    getSignStatus {
                        isapply.postValue(it)
                    }
                    getWeekRecordList {
                        currentWeekRecord.postValue(it)
                    }
                    getBalance {
                        balance.postValue(it)
                    }
                }
    }

}


