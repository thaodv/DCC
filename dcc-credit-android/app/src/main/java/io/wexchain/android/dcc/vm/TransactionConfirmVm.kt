package io.wexchain.android.dcc.vm

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.SystemClock
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.tools.TransHelper
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.UseProtect
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.gweiTowei
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/26.
 */
class TransactionConfirmVm(
        val tx: EthsTransactionScratch,
        val passport: Passport,
        val assetsRepository: AssetsRepository,
        var isedit: Boolean = false
) : UseProtect {
    override val type = ObservableField<LocalProtectType>()
    override val protectChallengeEvent = SingleLiveEvent<(Boolean) -> Unit>()

    val totalEth = tx.totalEth()

    val holding = ObservableField<String>()

    val enoughFunds = ObservableBoolean()

    val onPrivateChain = tx.currency.chain == Chain.JUZIX_PRIVATE

    val withTransferFee = tx.transferFeeRate != null

    val txSentEvent = SingleLiveEvent<Pair<EthsTransactionScratch, String>>()
    val txSendFailEvent = SingleLiveEvent<String>()

    val notEnoughFundsEvent = SingleLiveEvent<Void>()
    val busySendingEvent = SingleLiveEvent<Boolean>()

    fun loadHoldings() {
        val scratch = tx
        val dc = scratch.currency
        val agent = assetsRepository.getDigitalCurrencyAgent(dc)
        val ethereum = Currencies.Ethereum
        if (dc != ethereum && dc.chain == Chain.publicEthChain) {
            // erc20 on public eth chain
            val ethAgent = assetsRepository.getDigitalCurrencyAgent(ethereum)
            Single.zip<BigInteger, BigInteger, String>(
                    agent.getBalanceOf(scratch.from).retryWhen(RetryWithDelay.createSimple(3, 3000L)),
                    ethAgent.getBalanceOf(scratch.from).retryWhen(
                            RetryWithDelay.createSimple(
                                    3,
                                    3000L
                            )
                    ),
                    BiFunction { t1, t2 ->
                        val amount = dc.toDecimalAmount(t1)
                        val ethAmount = ethereum.toDecimalAmount(t2)
                        checkFunds(amount, ethAmount)
                        "${amount.currencyToDisplayStr()}${dc.symbol}\n${ethAmount.currencyToDisplayStr()}${ethereum.symbol}"
                    }
            )
        } else {
            agent.getBalanceOf(scratch.from)
                    .retryWhen(RetryWithDelay.createSimple(3, 3000L))
                    .map {
                        val amount = dc.toDecimalAmount(it)
                        if (dc == ethereum) {
                            checkEthFunds(amount)
                        } else {
                            checkErc20JuzixFunds(amount)
                        }
                        "${amount.currencyToDisplayStr()}${dc.symbol}"
                    }
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    holding.set(it)
                }, {
                    stackTrace(it)
                })
    }

    private fun checkErc20JuzixFunds(amount: BigDecimal) {
        AndroidSchedulers.mainThread().scheduleDirect {
            val scratch = tx
            enoughFunds.set(amount >= scratch.amount)
        }
    }


    private fun checkEthFunds(amount: BigDecimal) {
        AndroidSchedulers.mainThread().scheduleDirect {
            val scratch = tx
            enoughFunds.set(amount >= scratch.totalEth())
        }
    }

    private fun checkFunds(amount: BigDecimal, ethAmount: BigDecimal) {
        AndroidSchedulers.mainThread().scheduleDirect {
            val scratch = tx
            enoughFunds.set(amount >= scratch.amount && ethAmount >= scratch.maxTxFee())
        }
    }


    fun signAndSendTransaction() {
        if (!enoughFunds.get()) {
            notEnoughFundsEvent.call()
        } else {
            verifyProtect {
                var scratch = tx
                val dc = scratch.currency
                val agent = assetsRepository.getDigitalCurrencyAgent(dc)
                val p = passport
                assert(p.address == scratch.from)
                Single.just(p)
                        .observeOn(Schedulers.computation())
                        .map {
                            p.credential
                        }
                        .observeOn(Schedulers.io())
                        .flatMap { cre ->
                            val price = gweiTowei(scratch.gasPrice)

                            if (dc.chain == Chain.publicEthChain) {//公链
                                val eth = SharedPreferenceUtil.get(
                                        Extras.NEEDSAVEPENDDING,
                                        Extras.SAVEDPENDDING
                                ) as? EthsTransaction
                                if (null != eth) {
                                    if (isedit || scratch.CancelType) {//是否是编辑撤销操作 默认不是
                                        agent.editTransferTransaction(
                                                Single.just(eth.nonce),
                                                cre,
                                                scratch.to,
                                                dc.toIntExact(scratch.amount),
                                                price,
                                                scratch.gasLimit,
                                                scratch.remarks
                                        )
                                    } else {
                                        agent.getNonce(p.address).flatMap {
                                            if (it > eth.nonce) {
                                                agent.sendTransferTransaction(
                                                        cre,
                                                        scratch.to,
                                                        dc.toIntExact(scratch.amount),
                                                        price,
                                                        scratch.gasLimit,
                                                        scratch.remarks
                                                )
                                            } else {
                                                Single.error<Pair<BigInteger, String>>(
                                                        IllegalStateException("")
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    agent.sendTransferTransaction(
                                            cre,
                                            scratch.to,
                                            dc.toIntExact(scratch.amount),
                                            price,
                                            scratch.gasLimit,
                                            scratch.remarks
                                    )
                                }
                            } else {
                                agent.sendTransferTransaction(
                                        cre,
                                        scratch.to,
                                        dc.toIntExact(scratch.amount),
                                        price,
                                        scratch.gasLimit,
                                        scratch.remarks
                                )
                            }

                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            busySendingEvent.value = true
                        }
                        .doFinally {
                            busySendingEvent.value = false
                        }
                        .subscribe({
                            scratch.nonce = it.first
                            txSentEvent.value = scratch to it.second
                            TransHelper.afterTransSuc(scratch, it.second)

                            // 保存交易记录
                            App.get().passportRepository.getAddressByAddress(scratch.to).observeForever {
                                if (null != it) {
                                    App.get().passportRepository.addTransRecord(
                                            TransRecord(SystemClock.currentThreadTimeMillis(), scratch.to, it.shortName, it.avatarUrl, create_time = it.create_time, update_time = SystemClock.currentThreadTimeMillis(), is_add = 1))
                                } else {
                                    App.get().passportRepository.addTransRecord(
                                            TransRecord(SystemClock.currentThreadTimeMillis(), scratch.to, shortName = "", avatarUrl = "", create_time = SystemClock.currentThreadTimeMillis(), is_add = 0))
                                }
                            }
                        }, {
                            txSendFailEvent.value = it.message ?: "提交交易失败"
                        })

            }
        }
    }


}
