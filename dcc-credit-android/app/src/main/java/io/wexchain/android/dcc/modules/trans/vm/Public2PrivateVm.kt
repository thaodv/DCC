package io.wexchain.android.dcc.modules.trans.vm

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.getString
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.*
import io.wexchain.digitalwallet.proxy.JuzixErc20Agent
import io.wexchain.digitalwallet.util.*
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author Created by Wangpeng on 2018/7/30 17:32.
 * usage:
 */
class Public2PrivateVm {

    private lateinit var currency: DigitalCurrency
    var isEdit = false
    lateinit var tx: EthsTransaction

    val txTitle = ObservableField<String>()

    val toAddress = ObservableField<String>()

    /**
     * 最小转账金额
     */
    val poundge = ObservableField<String>()

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

    val dataInvalidatedEvent = SingleLiveEvent<Void>()

    val busyChecking = ObservableBoolean(false)

    val onPrivateChain = ObservableBoolean()

    var feeRate: BigDecimal? = null
    val transferFeeHintText = ObservableField<CharSequence>()

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
        maxTransactionFeeText.set(if (BigDecimal.ZERO == fee) "" else fee.toPlainString())
    }

    fun ensureDigitalCurrency(dc: DigitalCurrency, feeRate: String?) {
        if (this::currency.isInitialized) {
            if (currency != dc) {
                throw IllegalStateException()
            }
        } else {
            if (isEdit && null != tx) {
                // amount=tx.amount
            }

            currency = dc
            txTitle.set("${dc.symbol} 转账")
            updateGasPrice()

            onPrivateChain.set(dc.chain == Chain.JUZIX_PRIVATE)
            if (dc.chain == Chain.JUZIX_PRIVATE) {
                if (dc.symbol == Currencies.FTC.symbol) {
                    val decimal = feeRate!!.toBigDecimalSafe()
                    this.feeRate = decimal
                    val rdc = decimal.scaleByPowerOfTen(3).stripTrailingZeros().toPlainString()
                    val colored = SpannableString(rdc).apply {
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(App.get(), R.color.text_red)), 0, rdc.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    this.transferFeeHintText.set(colored)
                } else {
                    val hintText = getString(R.string.the_chain_of_dcc)
                    val dccFeeHint = SpannableStringBuilder(hintText).apply {
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(App.get(), R.color.text_red)), 9, hintText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    this.transferFeeHintText.set(dccFeeHint)
                }
            }
        }
    }

    private fun updateGasPrice() {
        App.get().assetsRepository.getDigitalCurrencyAgent(currency)
                .getGasPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var fpp = it
                    if (isEdit) {
                        fpp = maxOf(//
                                tx.gasPrice,
                                //it + BigDecimal("0.5").scaleByPowerOfTen(9).toBigInteger()
                                it
                        )
                    }
                    gasPrice.set(weiToGwei(fpp).stripTrailingZeros().toPlainString())
                    dataInvalidatedEvent.call()
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
                            dataInvalidatedEvent.call()
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
            inputNotSatisfiedEvent.value = getString(R.string.please_input_the_address_of_collector)
            return
        }
        val value = amount.get()?.toBigDecimalSafe()

        val temp_poundge = poundge.get()?.toBigDecimalSafe()

        if (value == null || value == BigDecimal.ZERO) {
            inputNotSatisfiedEvent.value = getString(R.string.please_input_the_transfer_amount)
            return
        } else if (value < temp_poundge) {
            inputNotSatisfiedEvent.value = getString(R.string.min_trans_count) + temp_poundge
            return
        }
        if (isEdit) {
            val gasprice = gasPrice.get()?.toBigDecimalSafe()
            if (gasprice == null || gasprice <= weiToGwei(tx.gasPrice)) {
                inputNotSatisfiedEvent.value = "Gas Price必须高于原交易的Gas price"
                return
            }
        }
        val dc = currency
        val isOnPrivate = onPrivateChain.get()
        val limit = if (isOnPrivate) {
            JuzixErc20Agent.GAS_LIMIT
        } else {
            val inputLimit = gasLimit.get()?.toBigIntegerSafe()
            if (inputLimit == null || inputLimit == BigInteger.ZERO) {
                inputNotSatisfiedEvent.value = getString(R.string.please_input_gaslimit)
                return
            }
            inputLimit
        }
        val scratch = if (isOnPrivate) {
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
        } else {
            val price = gasPrice.get()?.toBigDecimalSafe()
            if (price == null || value == BigDecimal.ZERO) {
                inputNotSatisfiedEvent.value = "GasPrice不能为空"
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
        this.txProceedEvent.value = scratch
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
                    //thus proceed as normal
                    this.txProceedEvent.value = scratch
                })
    }


}
