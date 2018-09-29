package io.wexchain.android.dcc.modules.bsx

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.getString
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.*
import io.wexchain.digitalwallet.proxy.JuzixErc20Agent
import io.wexchain.digitalwallet.util.*
import io.wexchain.ipfs.utils.doMain
import org.web3j.abi.FunctionEncoder
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author Created by Wangpeng on 2018/7/30 17:32.
 * usage:
 */
class BsxEthBuyVm {

    private lateinit var currency: DigitalCurrency

    lateinit var tx: EthsTransaction

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

    val dccPublic = Currencies.Ethereum

    val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccPublic)

    /**
     * 合约地址
     */
    var toAddress = ObservableField<String>()

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
            currency = dc
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
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(App.get(), R.color.text_red)), 8, hintText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    this.transferFeeHintText.set(dccFeeHint)
                }
            }
        }
    }

    fun updateGasPrice() {
        agent.getGasPrice()
                .doMain()
                .subscribe({
                    LogUtils.i("updateGasPrice", it.toString())
                    gasPrice.set(weiToGwei(it).stripTrailingZeros().toPlainString())
                    dataInvalidatedEvent.call()
                }, {
                    stackTrace(it)
                })
    }

    val r = FunctionEncoder.encode(Erc20Helper.investEthBsx())

    fun updateGasLimit(focus: Boolean) {
        if (focus) {
            val to = toAddress.get()!!
            val value = amount.get()?.toBigDecimalSafe()
            if (value == null || value == BigDecimal.ZERO) {
//                inputNotSatisfiedEvent.value = "amount not valid"
                return
            }

            val app = App.get()
            val from = app.passportRepository.getCurrentPassport()!!.address
            val dc = currency
            val gasPrice = BigDecimal(gasPrice.get())
            LogUtils.i("updateGasLimit-gasPrice", gasPrice.toPlainString())
            LogUtils.i("updateGasLimit-value", value.toPlainString())
            val scratch = EthsTransactionScratch(
                    currency = dc,
                    from = from,
                    to = to,
                    amount = value,
                    gasPrice = BigDecimal.ZERO,
                    gasLimit = BigInteger.ZERO,
                    remarks = r
            )
            agent.getGasLimit(scratch)
                    .doMain()
                    .onErrorReturn {
                        BigInteger.valueOf(100000)
                    }
                    .subscribe({
                        if (currency == dc && gasLimit.get().isNullOrEmpty()) {
                            LogUtils.i("gas-res:", it.toString())
                            gasLimit.set(it.toString())
                            dataInvalidatedEvent.call()
                        }
                    }, {
                        stackTrace(it)
                        LogUtils.i("获取gasLimit错误")
                        LogUtils.i(it.message ?: "获取gasLimit错误")
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
        LogUtils.i("amount", value!!.toPlainString())
        val temp_poundge = poundge.get()?.toBigDecimalSafe()
        LogUtils.i("poundge", temp_poundge!!.toPlainString())
        if (value == BigDecimal.ZERO) {
            inputNotSatisfiedEvent.value = getString(R.string.please_input_the_transfer_amount)
            return
        } else if (value < temp_poundge) {
            inputNotSatisfiedEvent.value = getString(R.string.min_trans_count) + temp_poundge
            return
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
                    r,
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
                    r
            )
        }
        agent.getGasLimit(scratch)
                .doMain()
                .doOnSubscribe {
                    busyChecking.set(true)
                }
                .doFinally {
                    busyChecking.set(false)
                }
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
