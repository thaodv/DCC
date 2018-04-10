package io.wexchain.android.dcc.vm

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.UseProtect
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
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
        val assetsRepository: AssetsRepository
):UseProtect {
    override val type = ObservableField<LocalProtectType>()
    override val protectChallengeEvent = SingleLiveEvent<(Boolean) -> Unit>()

    val totalEth = tx.totalEth()

    val holding = ObservableField<String>()

    val enoughFunds = ObservableBoolean()

    val onPrivateChain = tx.currency.chain == Chain.JUZIX_PRIVATE

    val withTransferFee = tx.transferFeeRate!=null

    val txSentEvent = SingleLiveEvent<Pair<EthsTransactionScratch, String>>()

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
                    ethAgent.getBalanceOf(scratch.from).retryWhen(RetryWithDelay.createSimple(3, 3000L)),
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
                        if(dc == ethereum) {
                            checkEthFunds(amount)
                        }else{
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
                val scratch = tx
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
                        .flatMap {
                            val price = gweiTowei(scratch.gasPrice)
                            agent.sendTransferTransaction(it, scratch.to, dc.toIntExact(scratch.amount), price, scratch.gasLimit, scratch.remarks)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            busySendingEvent.value = true
                        }
                        .doFinally {
                            busySendingEvent.value = false
                        }
                        .subscribe({
                            txSentEvent.value = scratch to it
                        }, {
                            stackTrace(it)
                        })

            }
        }
    }
}