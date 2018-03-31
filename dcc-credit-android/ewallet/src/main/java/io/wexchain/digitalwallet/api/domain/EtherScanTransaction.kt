package io.wexchain.digitalwallet.api.domain

import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.util.toBigIntegerSafe
import org.web3j.utils.Numeric
import java.math.BigInteger


/**
 * Created by sisel on 2018/1/17.
 */

data class EtherScanTransaction(
        val blockNumber: Long,
        val timeStamp: Long,
        val hash: String,
        val nonce: String,
        val blockHash: String,
        val transactionIndex: String,
        val from: String,
        val to: String,
        val value: String,
        val gas: BigInteger,
        val gasPrice: BigInteger,
        val isError: String,
        val txreceipt_status: String,
        val input: String,
        val contractAddress: String,
        val cumulativeGasUsed: BigInteger,
        val gasUsed: BigInteger,
        val confirmations: Long
) {
    fun gotError(): Boolean {
        return isError != "0"
    }

    fun hasReceipt(): Boolean {
        return txreceipt_status == "1"
    }

    fun toDigitalTransaction(dc: DigitalCurrency = Currencies.Ethereum): EthsTransaction {
        val transactionType = deferTransactionType()
        return EthsTransaction(
                digitalCurrency = dc,
                txId = this.hash,
                from = this.from,
                to = this.to,
                amount = this.value.toBigIntegerSafe(),
                transactionType = transactionType,
                remarks = if (transactionType == EthsTransaction.TYPE_TRANSFER) String(Numeric.hexStringToByteArray(this.input), Charsets.UTF_8) else "",
                time = this.timeStamp * 1000L,
                blockNumber = this.blockNumber,
                gas = this.gas,
                gasUsed = this.gasUsed,
                gasPrice = this.gasPrice,
                status = deferStatus()
        )
    }

    private fun deferStatus(): EthsTransaction.Status {
        //todo more consideration #confirmations #isError
        return if (gotError()) {
            EthsTransaction.Status.FAILED
        } else {
            EthsTransaction.Status.MINED
        }
//        return when {
//            gotError() -> EthsTransaction.Status.FAILED
//            confirmations >= 12 -> EthsTransaction.Status.CONFIRMED
//            hash.isNotEmpty() -> EthsTransaction.Status.MINED
//            else -> EthsTransaction.Status.PENDING
//        }
    }

    private fun deferTransactionType(): String {
        return if (this.to.isEmpty() || Numeric.toBigInt(this.to) == BigInteger.ZERO) {
            EthsTransaction.TYPE_CREATE_CONTRACT
        } else {
            EthsTransaction.TYPE_TRANSFER
        }
    }
}