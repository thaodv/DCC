package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.databinding.ObservableBoolean
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
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
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
    }

    private var pv: List<EthsTransaction>? = null

    private var hol: List<EthsTransaction>? = null

    private val history = AutoLoadLiveData<List<EthsTransaction>> {
        val addr = address
        assetsRepository.getDigitalCurrencyAgent(dc)
                .listTransactionsOf(addr, 0, Long.MAX_VALUE)
                .doOnSuccess {
                    hol = it
                    pv = pending.value
                }
                .toFlowable()
    }

    private val pending: LiveData<List<EthsTransaction>> = assetsRepository.pendingTxList
            .filter {
                val tdc = it.digitalCurrency
                tdc.chain == dc.chain && tdc.contractAddress == dc.contractAddress
            }
            .observing(
                    doOnChange = {
                        if (it != pv) {
                            history.reload()
                        }
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

    private fun startFetchPendingList() {
        val agent = assetsRepository.getDigitalCurrencyAgent(dc)
        fetchDisposable = Single.just(pending)
                .flatMap {
                    var pl =   (pending.value ?: emptyList()).toMutableList()
                    val timer = Single.timer(30, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                    if (!(pl != null && pl.isNotEmpty())) {
                        if (dc.chain == Chain.publicEthChain) {//公链
                            val eth = SharedPreferenceUtil.get(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction
                            if (null != eth) {
                               pl.add(0, eth)
                                App.get().assetsRepository.pushPendingTx(eth)
                            }
                    }}

                    if (pl != null && pl.isNotEmpty()) {
                        Single
                                .zip(pl.map {
                                    val txhash = it.txId
                                    val nn = it.nonce
                                    agent.transactionReceipt(txhash)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnSuccess {
                                                assetsRepository.removePendingTx(txhash, nn)
                                                pendingTxDoneEvent.value = txhash
                                            }
                                            .map { txhash }
                                            .onErrorReturn {
                                                ""
                                            }
                                }.map {
                                    if (hol != null && !hol!!.isEmpty()) {
                                    for (i in hol!!) {
                                        assetsRepository.removePendingTx(i.txId, i.nonce)
                                    }
                                }
                                    /*  val addr = address
                                      assetsRepository.getDigitalCurrencyAgent(dc)
                                          .listTransactionsOf(addr, 0, Long.MAX_VALUE)
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .doOnSuccess {
                                              if (it!=null&&!it.isEmpty()){
                                                  for (i in it){
                                                      assetsRepository.removePendingTx(i.txId,i.nonce)
                                                  }
                                              }
                                          }*/
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
        p + h
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
