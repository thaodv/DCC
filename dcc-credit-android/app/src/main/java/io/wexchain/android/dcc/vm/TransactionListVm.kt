package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.databinding.ObservableBoolean
import android.support.annotation.MainThread
import com.tencent.mm.opensdk.utils.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.wexchain.android.common.*
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.trans.activity.Public2PrivateActivity
import io.wexchain.android.dcc.tools.AutoLoadLiveData
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.view.dialog.WaitTransDialog
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import java.math.BigInteger
import java.util.concurrent.TimeUnit

/**
 * Created by sisel on 2018/1/24.
 */
class TransactionListVm(application: Application) : AndroidViewModel(application) {

    private val assetsRepository = getApplication<App>().assetsRepository

    lateinit var dc: DigitalCurrency
        private set

    private lateinit var address: String

    val showAll = ObservableBoolean(true)
    val empty = ObservableBoolean(true)
    val viewMoreEvent = SingleLiveEvent<Void>()
    val pendingTxDoneEvent = SingleLiveEvent<String>()

    fun ensure(dc: DigitalCurrency, address: String, showAll: Boolean) {
        this.dc = dc
        this.address = address
        this.showAll.set(showAll)
       /* assetsRepository.getDigitalCurrencyAgent(dc)
            .listTransactionsOf(address, 0, Long.MAX_VALUE)
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                hol = it
                pv = pending.value
               //Log.e("sssssssssssssss","ensure")
                   historyfilter()
                // startFetchPendingList()
            }.subscribe()*/
    }

    private var pv: List<EthsTransaction>? = null

    private var hol: List<EthsTransaction>? = null

    private val history = AutoLoadLiveData<List<EthsTransaction>> {
        val addr = address
        assetsRepository.getDigitalCurrencyAgent(dc)
            .listTransactionsOf(addr, 0, Long.MAX_VALUE)
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                hol = it
                pv = pending.value
               //Log.e("sssssssssssssss","addr")
              //  historyfilter()
               // startFetchPendingList()
            }
            .toFlowable()
    }


    @MainThread
    fun historyfilter(){

        if (hol != null && !hol!!.isEmpty()) {
           //Log.e("sssssssssssssss","historyfilter")
            for (i in hol!!) {
                assetsRepository.removePendingTx(i.txId, i.nonce)
            }
        }else{
           //Log.e("sssssssssssssss","historyfilter  null")
        }
    }

    private val pending: LiveData<List<EthsTransaction>> = assetsRepository.pendingTxList
        .filter {
            val tdc = it.digitalCurrency
            tdc.chain == dc.chain && tdc.contractAddress == dc.contractAddress
        }
        .observing(
            doOnChange = {
               // historyfilter()
                history.reload()
            },
            doOnActive = {
                fetchPendingEnabled = true
                startFetchPendingList()
            },
            doOnInactive = {
                fetchPendingEnabled = false
                stopFetchPendingList()
            }
        )

    private var fetchPendingEnabled = false
    private var fetchDisposable: Disposable? = null

    private fun stopFetchPendingList() {
        fetchDisposable?.dispose()
    }

    var isFirst: Boolean = true

    fun isInHistory(eth: EthsTransaction): Boolean {
       //Log.e("sssssssssssssss","查列表")
        if (hol != null && !hol!!.isEmpty()) {
            for (i in hol!!) {
                if (eth.txId .equals(i.txId)) {
                    return true
                   //Log.e("sssssssssssssss","查列表1")
                }
            }
            return false
           //Log.e("sssssssssssssss","查列表2")
        }
       //Log.e("sssssssssssssss","查列表3")
        return false

    }
    var isload: Boolean = true

    private fun startFetchPendingList() {
        val agent = assetsRepository.getDigitalCurrencyAgent(dc)
      /*  var pl = (pending.value ?: emptyList()).toMutableList()
        if (!(pl != null && pl.isNotEmpty())) {
            if (dc.chain == Chain.publicEthChain && isFirst) {//公链
                val eth = SharedPreferenceUtil.get(
                    Extras.NEEDSAVEPENDDING,
                    Extras.SAVEDPENDDING
                ) as? EthsTransaction
                if (null != eth) {
                    if(!isInHistory(eth)){
                        pl.add(0, eth)
                        assetsRepository.pushPendingTx(eth)
                        isFirst=false
                        isload=false
                        //Log.e("sssssssssssssss","加内存")
                    }
                    isFirst = false
                    pl.add(0, eth)
                    assetsRepository.pushPendingTx(eth)
                }
            }
        }*/
        fetchDisposable = Single.just(pending)
            .flatMap {
                val timer = Single.timer(30, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                if (hol != null && !hol!!.isEmpty()) {
                    for (i in hol!!) {
                        assetsRepository.removePendingTx(i.txId, i.nonce)
                        //pendingTxDoneEvent.value = i.txId
                        //  pl .removeAt(0)
                    }
                }
                var p2 = (pending.value ?: emptyList()).toMutableList()
                if (dc.chain == Chain.publicEthChain && isFirst&&p2 != null && p2.isNotEmpty()) {//公链
                    p2.map {
                        val txhash = it.txId
                        val nn = it.nonce
                        agent.getNonce(App.get().passportRepository.getCurrentPassport()!!.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it > nn) {
                                // toCreateTransaction()
                                assetsRepository.removePendingTx(txhash, nn)
                                pendingTxDoneEvent.value = txhash
                                history.reload()
                            }
                        }, {})
                    }
                     }
                if (p2 != null && p2.isNotEmpty()) {
                    Single
                        .zip(p2.map {
                            val txhash = it.txId
                            val nn = it.nonce
                            agent.transactionReceipt(txhash)
                                .observeOn(AndroidSchedulers.mainThread())

                                .doOnSuccess {
                                    val blockNumber = it.blockNumber
                                    if (blockNumber != null && !blockNumber.equals("") && !blockNumber.contains("None")) {
                                        assetsRepository.removePendingTx(txhash, nn)
                                        pendingTxDoneEvent.value = txhash
                                        history.reload()
                                    }
                                }
                                .map { txhash }
                                .onErrorReturn {
                                    ""
                                }
                        }.map {
                            it
                        }) {
                            it
                        }
                        .zipWith(timer, BiFunction { t1, t2 ->
                            t2
                        })
                } else {
                    timer
                }
            }
            .repeatUntil {
                !fetchPendingEnabled
            }
            .subscribe()
    }

    val list = zipLiveData(history, pending) { h, p ->
        /*if (hol != null && !hol!!.isEmpty()) {
            for (i in hol!!) {
                assetsRepository.removePendingTx(i.txId, i.nonce)
                p.filter { it.txId != i.txId }.filter { it.nonce != i.nonce }
            }
        }*/
        var pp=p.toMutableList()
       //Log.e("sssssssssssssss","list "+h.size+pp.size)
        historyfilter()

        if (h != null && !h.isEmpty()) {
           for (i in h) {
               assetsRepository.removePendingTx(i.txId, i.nonce)
               pp=pp.filter { !it.txId .equals(i.txId) }.toMutableList()
              /* if(pp.size>0&&pp[0].txId.equals(i.txId)){
                   pp.removeAt(0)
               }*/
           }
       }
        pp + h
    }


    fun viewMore() {
        if (!showAll.get()) {
            viewMoreEvent.call()
        }
    }

    companion object {
        @JvmStatic
        fun otherOf(ownerAddress: String, tx: EthsTransaction): String {
            return when (ownerAddress) {
                tx.from -> tx.to
                tx.to -> tx.from
                else -> ""
            }
        }

        @JvmStatic
        fun txValue(ownerAddress: String, tx: EthsTransaction): String {
            val txAmountStr = tx.digitalCurrency.toDecimalAmount(tx.amount).currencyToDisplayStr()
            return when (ownerAddress) {
                tx.from -> "-$txAmountStr ${tx.digitalCurrency.symbol}"
                tx.to -> "+$txAmountStr ${tx.digitalCurrency.symbol}"
                else -> ""
            }
        }
    }

}
