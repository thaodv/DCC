package io.wexchain.android.dcc.modules.garden.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.tools.check
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.TaskList
import io.wexchain.dccchainservice.domain.WeekRecord
import io.wexchain.dccchainservice.type.StatusType
import io.wexchain.dccchainservice.type.TaskCode
import io.wexchain.dccchainservice.type.TaskType
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/11/7.
 */
class GardenTaskVm : ViewModel() {

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    val backWallet = SingleLiveEvent<Void>()
    val syncIpfs = SingleLiveEvent<Void>()
    val toGardenList = SingleLiveEvent<Void>()
    val shareWechat = SingleLiveEvent<Void>()
    val idCert = SingleLiveEvent<Void>()
    val bankCert = SingleLiveEvent<Void>()
    val cmCert = SingleLiveEvent<Void>()
    val tnCert = SingleLiveEvent<Void>()
    val openIpfs = SingleLiveEvent<Void>()

    val taskList = MutableLiveData<List<TaskList>>()
            .apply {
                getTaskList()
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

    val isOpenIpfs = MutableLiveData<Pair<Boolean, String?>>()
            .apply {
                IpfsOperations.getIpfsKey()
                        .checkKey()
                        .io_main()
                        .subscribeBy {
                            if (it.isEmpty()) {
                                this.postValue(false to null)
                            } else {
                                this.postValue(true to it)
                            }
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

    fun getSignStatus(event: (Boolean) -> Unit) {
        GardenOperations
                .refreshToken {
                    api.queryTodayRecord(it).check()
                }
                .map {
                    true
                }
                .onErrorReturn {
                    false
                }
                .io_main()
                .subscribeBy {
                    event.invoke(it)
                }
    }

    fun getWeekRecordList(event: (List<WeekRecord>) -> Unit) {
        GardenOperations
                .refreshToken {
                    api.currentWeekRecord(it).check()
                }
                .doMain()
                .subscribeBy {
                    event.invoke(it)
                }
    }

    fun getTaskList() {
        GardenOperations
                .refreshToken {
                    api.getTaskList(it).check()
                }
                .doMain()
                .subscribeBy {
                    taskList.postValue(it)
                    checkOlderUser(it)
                }
    }

    private fun checkOlderUser(list: List<TaskList>) {
        Observable.just(isOpenIpfs.value != null && isOpenIpfs.value!!.first)
                .filter {
                    it
                }
                .flatMap {
                    Observable.fromIterable(list)
                }
                .filter {
                    it.category == TaskType.NEWBIE_TASK
                }
                .map {
                    it.taskList
                }
                .flatMap {
                    Observable.fromIterable(it)
                }
                .filter {
                    it.code == TaskCode.OPEN_CLOUD_STORE && it.status == StatusType.UNFULFILLED
                }
                .flatMap {
                    GardenOperations.completeTask(it.code).toObservable()
                }
                .subscribeBy {
                    refreshTaskList()
                }
    }

    fun refreshTaskList() {
        getTaskList()
    }

    fun withSign() {
        if (isapply.value != false) {
            return
        }
        GardenOperations
                .refreshToken {
                    api.apply(it).check()
                }
                .doMain()
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

    fun checkFulfilled(task: TaskList.Task?, event: () -> Unit) {
        if (task != null && task.status == StatusType.UNFULFILLED) {
            event.invoke()
        }
    }

    fun backWallet(task: TaskList.Task?) {
        checkFulfilled(task) {
            backWallet.call()
        }
    }

    fun syncIpfs(task: TaskList.Task?) {
        checkFulfilled(task) {
            syncIpfs.call()
        }
    }

    fun idCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            idCert.call()
        }
    }

    fun bankCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            bankCert.call()
        }
    }

    fun cmCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            cmCert.call()
        }
    }

    fun tnCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            tnCert.call()
        }
    }

    fun openIpfs(task: TaskList.Task?) {
        checkFulfilled(task) {
            openIpfs.call()
        }
    }

    fun toGardenList() {
        toGardenList.call()
    }

    fun shareWechat() {
        shareWechat.call()
    }


}


