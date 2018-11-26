package io.wexchain.android.dcc.modules.garden.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.TaskList
import io.wexchain.dccchainservice.domain.WeekRecord
import io.wexchain.dccchainservice.type.StatusType
import io.wexchain.dccchainservice.type.TaskCode
import io.wexchain.dccchainservice.type.TaskType
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main
import java.util.concurrent.TimeUnit

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
                getIpfsKey{
                    if (it.isEmpty()) {
                        this.postValue(false to null)
                    } else {
                        this.postValue(true to it)
                    }
                }
            }

    private fun getIpfsKey(event: (String) -> Unit) {
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .subscribeBy {
                    event(it)
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
                .doOnSuccess {
                    checkOlderUser(it)
                    checkCreateWallet(it)
                    checkCert(it)
                }
                .subscribeBy {
                    taskList.postValue(it)
                }
    }

    fun getTaskList2() {
        GardenOperations
                .refreshToken {
                    api.getTaskList(it).check()
                }
                .doMain()
                .doOnSuccess {
                    taskList.postValue(it)
                }
                .subscribe()
    }

    private fun checkCreateWallet(it: List<TaskList>) {
        Observable.fromIterable(it)
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
                    it.code == TaskCode.CREATE_WALLET
                }
                .filter {
                    it.status == StatusType.UNFULFILLED
                }
                .flatMap {
                    GardenOperations.completeTask(TaskCode.CREATE_WALLET).toObservable()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    refreshTaskList()
                }
                .subscribe()
    }

    private fun checkOlderUser(list: List<TaskList>) {
        IpfsOperations.getIpfsKey()
                .checkKey()
                .toObservable()
                .filter {
                    it.isNotEmpty()
                }
                .flatMap {
                    Observable.fromIterable(list)
                }
                .zipWith(checkBackTask(list))
                .filter {
                    it.first.category == TaskType.NEWBIE_TASK
                }
                .map {
                    it.first.taskList
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    refreshTaskList()
                }
                .subscribe()
    }

    fun checkCert(list: List<TaskList>) {
        val idstatus = CertOperations.getCertStatus(CertificationType.ID)
        val bankstatus = CertOperations.getCertStatus(CertificationType.BANK)
        val cmstatus = CertOperations.getCertStatus(CertificationType.MOBILE)
        val tnstatus = CertOperations.getCertStatus(CertificationType.TONGNIU)

        Observable.fromIterable(list)
                .filter {
                    it.category == TaskType.CERT_TASK
                }
                .map {
                    it.taskList
                }
                .flatMap {
                    Observable.fromIterable(it)
                }
                .filter {
                    it.status == StatusType.UNFULFILLED
                }
                .flatMap {
                    when (it.code) {
                        TaskCode.ID -> Observable.just(it.code to idstatus)
                        TaskCode.BANK_CARD -> Observable.just(it.code to bankstatus)
                        TaskCode.COMMUNICATION_LOG -> Observable.just(it.code to cmstatus)
                        TaskCode.TN_COMMUNICATION_LOG -> Observable.just(it.code to tnstatus)
                        else -> throw Exception("")
                    }
                }
                .filter {
                    it.second == UserCertStatus.DONE || it.second == UserCertStatus.TIMEOUT
                }
                .flatMap {
                    GardenOperations.completeTask(it.first).toObservable()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    refreshTaskList()
                }
                .subscribe()
    }

    private fun checkBackTask(list: List<TaskList>): Observable<ChangeOrder> {
        return Observable.fromIterable(list)
                .filter {
                    it.category == TaskType.BACKUP_TASK
                }
                .map {
                    it.taskList
                }
                .flatMap {
                    Observable.fromIterable(it)
                }
                .filter {
                    it.code != TaskCode.BACKUP_WALLET
                }
                .filter {
                    it.status == StatusType.UNFULFILLED
                }
                .flatMap {
                    when (it.code) {
                        TaskCode.BACKUP_ID -> IpfsOperations.getIpfsToken(ChainGateway.BUSINESS_ID).toObservable().zipWith(Observable.just(it.code))
                        TaskCode.BACKUP_BANK_CARD -> IpfsOperations.getIpfsToken(ChainGateway.BUSINESS_BANK_CARD).toObservable().zipWith(Observable.just(it.code))
                        TaskCode.BACKUP_COMMUNICATION_LOG -> IpfsOperations.getIpfsToken(ChainGateway.BUSINESS_COMMUNICATION_LOG).toObservable().zipWith(Observable.just(it.code))
                        TaskCode.BACKUP_TN_COMMUNICATION_LOG -> IpfsOperations.getIpfsToken(ChainGateway.TN_COMMUNICATION_LOG).toObservable().zipWith(Observable.just(it.code))
                        else -> throw  Exception("no find code ")
                    }
                }
                .filter {
                    it.first.result.toString() != "0x"
                }
                .map {
                    it.second
                }
                .flatMap {
                    GardenOperations.completeTask(it).toObservable()
                }


    }

    fun refreshTaskList(event: () -> Unit = {}) {
        getTaskList2()
        getBalance {
            balance.postValue(it)
        }
        getIpfsKey{
            if (it.isEmpty()) {
                isOpenIpfs.postValue(false to null)
            } else {
                isOpenIpfs.postValue(true to it)
            }
        }
        Observable.timer(2,TimeUnit.SECONDS)
                .subscribe {
                    event()
                }
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
            syncIpfs.call()
        }
    }

    fun bankCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            syncIpfs.call()
        }
    }

    fun cmCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            syncIpfs.call()
        }
    }

    fun tnCert(task: TaskList.Task?) {
        checkFulfilled(task) {
            syncIpfs.call()
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


