package io.wexchain.digitalwallet

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import java.io.Serializable
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/16.
 * Ethereum standard transaction
 */
data class EthsTransaction(
        @JvmField val digitalCurrency: DigitalCurrency,
        /**
         * tx hash
         */
        @JvmField val txId: String,
        @JvmField val from: String,
        @JvmField val to: String,
        /**
         * should be positive
         */
        @JvmField val amount: BigInteger,
        @JvmField val transactionType: String,
        @JvmField val remarks: String? = null,
        @JvmField val time: Long,
        @JvmField var blockNumber: Long,
        @JvmField var gas: BigInteger,
        @JvmField var gasUsed: BigInteger,
        @JvmField var gasPrice: BigInteger,
        @JvmField var status: Status
) : Serializable {
    enum class Status {
        PENDING,
        MINED,
        CONFIRMED,
        FAILED
        ;

        fun toDisplayStr(): String {
            return when (this) {
                EthsTransaction.Status.PENDING -> "未上链"
                EthsTransaction.Status.MINED -> "已上链"
                EthsTransaction.Status.CONFIRMED -> "已确认"
                EthsTransaction.Status.FAILED -> "失败"
            }
        }
    }

    fun txFeeStr(): String {
        return "${(Currencies.Ethereum.toDecimalAmount(gasPrice) * gasUsed.toBigDecimal()).stripTrailingZeros().toPlainString()} ${Currencies.Ethereum.symbol}"
    }

    fun txIdLinked(): CharSequence {
        return if (digitalCurrency.chain == Chain.JUZIX_PRIVATE) {
            txId
        } else {
            SpannableStringBuilder(txId).apply {
                setSpan(URLSpan(etherScanTxUrl(txId, digitalCurrency.chain)), 0, txId.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun isPending(): Boolean {
        return status == Status.PENDING
    }

    fun onPrivateChain(): Boolean {
        return this.digitalCurrency.chain == Chain.JUZIX_PRIVATE
    }

    companion object {
        const val TYPE_TRANSFER = "转账"
        const val TYPE_CREATE_CONTRACT = "创建合约"
        fun etherScanTxUrl(txId: String, chain: Chain): String {
            return when (chain) {
                Chain.Ethereum -> {
                    "https://etherscan.io/tx/$txId"
                }
                else -> {
                    "https://${chain.name.toLowerCase()}.etherscan.io/tx/$txId"
                }
            }
        }

    }
}