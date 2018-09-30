package io.wexchain.digitalwallet

import io.wexchain.digitalwallet.util.computeEthTxFee
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/22.
 */
data class EthsTransactionScratch(
        val currency: DigitalCurrency,
        val from: String,
        var to: String,
        /**
         * in currency unit with decimals
         */
        var amount: BigDecimal,
        var gasPrice: BigDecimal,
        var gasLimit: BigInteger,
        var remarks: String? = null,
        val transferFeeRate: BigDecimal? = null,
        var nonce: BigInteger=BigInteger("1"),

         var CancelType:Boolean=false //默认不是撤销操作

        ) : Serializable {
    /**
     * 预计最大矿工费
     */
    fun maxTxFee(): BigDecimal {
        return if (currency.chain == Chain.JUZIX_PRIVATE) {
            BigDecimal.ZERO
        } else {
            computeEthTxFee(gasPrice, gasLimit)
        }
    }

    fun maxTxFeeStr() = "${maxTxFee().toPlainString()}${Currencies.Ethereum.symbol}"
    fun totalEth(): BigDecimal? {
        return if (currency == Currencies.Ethereum) {
            maxTxFee() + amount
        } else null
    }

    fun expectedReceiveAmountText(): String {
        return if(transferFeeRate == null){
            "${amount.stripTrailingZeros().toPlainString()}${currency.symbol}"
        }else{
            val ra = ((BigDecimal.ONE - transferFeeRate) * amount).stripTrailingZeros()
            "${ra.toPlainString()} ${currency.symbol}"
        }
    }

    fun expectedTransferFeeText():String{
        return if(transferFeeRate == null){
            ""
        }else{
            val fa = (transferFeeRate * amount).stripTrailingZeros()
            "${fa.toPlainString()} ${currency.symbol}"
        }
    }
}
