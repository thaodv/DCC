package io.wexchain.android.dcc.vm

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.proxy.JuzixErc20Agent
import io.wexchain.digitalwallet.util.*
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/22.
 */
class TransactionVm {
    private lateinit var currency: DigitalCurrency

    val txTitle = ObservableField<String>()

    val toAddress = ObservableField<String>()

    val amount = ObservableField<String>()

    val remarks = ObservableField<String>()

    /**
     * input gas price in gwei
     */
    val gasPrice = ObservableField<String>()

    val gasLimit = ObservableField<String>()

    /**
     * fee in eth
     */
    private var maxTransactionFee = BigDecimal.ZERO

    val maxTransactionFeeText = ObservableField<String>()

    val txProceedEvent = SingleLiveEvent<EthsTransactionScratch>()

    val inputNotSatisfiedEvent = SingleLiveEvent<String>()

    val busyChecking = ObservableBoolean(false)

    val onPrivateChain = ObservableBoolean()

    var feeRate:BigDecimal? = null
    val transferFeeHintText = ObservableField<String>()

    init {
        val feeChange: Observable.OnPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
                computeFee()
            }
        }
        this.gasPrice.addOnPropertyChangedCallback(feeChange)
        this.gasLimit.addOnPropertyChangedCallback(feeChange)
    }

    private fun computeFee() {
        val price: BigDecimal? = gasPrice.get()?.toBigDecimalSafe()
        val limit: BigInteger? = gasLimit.get()?.toBigIntegerSafe()
        val fee = if (price != null && price != BigDecimal.ZERO && limit != null && limit != BigDecimal.ZERO) {
            // gwei to wei  then  multiply then to eth
            computeEthTxFee(price, limit)
        } else {
            BigDecimal.ZERO
        }
        maxTransactionFee = fee
        maxTransactionFeeText.set(if (BigDecimal.ZERO == fee) "" else fee.toPlainString() + "ETH")
    }

    fun ensureDigitalCurrency(dc: DigitalCurrency, feeRate: String?) {
        if (this::currency.isInitialized) {
            if (currency != dc) {
                throw IllegalStateException()
            }
        } else {
            currency = dc
            txTitle.set("${dc.symbol} 转账")
            updateGasPrice()
            onPrivateChain.set(dc.chain == Chain.JUZIX_PRIVATE)
            if (dc.chain == Chain.JUZIX_PRIVATE && dc.symbol == Currencies.FTC.symbol){
                val decimal = feeRate!!.toBigDecimalSafe()
                this.feeRate = decimal
                val rdc = decimal.scaleByPowerOfTen(3).stripTrailingZeros().toPlainString()
                this.transferFeeHintText.set(rdc)
            }
        }
    }

    private fun updateGasPrice() {
        App.get().assetsRepository.getDigitalCurrencyAgent(currency)
                .getGasPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    gasPrice.set(weiToGwei(it).stripTrailingZeros().toPlainString())
                }, {
                    stackTrace(it)
                })
    }

    fun updateGasLimit(focus: Boolean) {
        if (focus) {
            val to = toAddress.get()
            if (to == null || !isEthAddress(to)) {
//                inputNotSatisfiedEvent.value = "to address not valid"
                return
            }
            val value = amount.get()?.toBigDecimalSafe()
            if (value == null || value == BigDecimal.ZERO) {
//                inputNotSatisfiedEvent.value = "amount not valid"
                return
            }
            val r = remarks.get()
            val app = App.get()
            val from = app.passportRepository.getCurrentPassport()!!.address
            val dc = currency
            val scratch = EthsTransactionScratch(
                    currency = dc,
                    from = from,
                    to = to,
                    amount = value,
                    gasPrice = BigDecimal.ZERO,
                    gasLimit = BigInteger.ZERO,
                    remarks = r
            )
            app.assetsRepository.getDigitalCurrencyAgent(dc)
                    .getGasLimit(scratch)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn { BigInteger.valueOf(100000) }
                    .subscribe({
                        if (currency == dc && gasLimit.get().isNullOrEmpty()) {
                            gasLimit.set(it.toString())
                        }
                    }, {
                        stackTrace(it)
                    })
        }
    }

    fun checkAndProceed(from: String?) {
        from ?: return
        val to = toAddress.get()
        if (to == null || !isEthAddress(to)) {
            inputNotSatisfiedEvent.value = "to address not valid"
            return
        }
        val value = amount.get()?.toBigDecimalSafe()
        if (value == null || value == BigDecimal.ZERO) {
            inputNotSatisfiedEvent.value = "amount not valid"
            return
        }
        val dc = currency
        val isOnPrivate = onPrivateChain.get()
        val limit = if (isOnPrivate){
            JuzixErc20Agent.GAS_LIMIT
        } else {
            val inputLimit = gasLimit.get()?.toBigIntegerSafe()
            if (inputLimit == null || inputLimit == BigInteger.ZERO) {
                inputNotSatisfiedEvent.value = "gas limit not valid"
                return
            }
            inputLimit
        }
        val scratch = if (isOnPrivate){
            EthsTransactionScratch(
                    dc,
                    from,
                    to,
                    value,
                    JuzixErc20Agent.GAS_PRICE.toBigDecimal().scaleByPowerOfTen(-9),
                    limit,
                    remarks.get(),
                    feeRate
            )
        }else {
            val price = gasPrice.get()?.toBigDecimalSafe()
            if (price == null || value == BigDecimal.ZERO) {
                inputNotSatisfiedEvent.value = "gas price not valid"
                return
            }
            EthsTransactionScratch(
                    dc,
                    from,
                    to,
                    value,
                    price,
                    limit,
                    remarks.get()
            )
        }
        App.get().assetsRepository.getDigitalCurrencyAgent(dc)
                .getGasLimit(scratch)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { busyChecking.set(true) }
                .doFinally { busyChecking.set(false) }
                .subscribe({
                    if (it > limit) {
                        this.inputNotSatisfiedEvent.value = "Gas Limit不足${it}请重新设置"
                    } else {
                        this.txProceedEvent.value = scratch
                    }
                }, {
                    stackTrace(it)
                    //cannot get gas limit
                    //thus scanSucceedAndProceed as normal
                    this.txProceedEvent.value = scratch
                })
    }
}