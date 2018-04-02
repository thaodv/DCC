package io.wexchain.digitalwallet.proxy

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.api.*
import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/16.
 */
class EthereumAgent(
        rpcAgent: EthsRpcAgent,
        txAgent: EthsTxAgent
) : EthCurrencyAgent(rpcAgent, txAgent) {
    override fun getNonce(address: String): Single<BigInteger> {
        return rpcAgent.getTransactionCount(address, "pending")
    }

    override fun encodeTransfer(nonce: BigInteger, from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String?): String {
        val remarksHex = if (remarks == null) "" else Numeric.toHexString(remarks.toByteArray(Charsets.UTF_8))
        val gas = if (remarks == null || gasLimit < EthereumAgent.ethTransferGasLimit) EthereumAgent.ethTransferGasLimit else gasLimit
        return Numeric.toHexString(TransactionEncoder.signMessage(RawTransaction.createTransaction(nonce, gasPrice, gas, to, amount, remarksHex), from))!!
    }

    override fun getGasLimit(ethsTransactionScratch: EthsTransactionScratch): Single<BigInteger> {
        val txAmount = ethsTransactionScratch.currency.toIntExact(ethsTransactionScratch.amount)
        return rpcAgent.estimateGas(EthJsonTxScratch(
                from = ethsTransactionScratch.from,
                to = ethsTransactionScratch.to,
                value = Numeric.toHexStringWithPrefix(txAmount),
                data = Numeric.toHexString((ethsTransactionScratch.remarks ?: "").toByteArray())
        ))
    }

    override fun getBalanceOf(address: String): Single<BigInteger> {
        return rpcAgent.getBalanceOf(address)
    }

    companion object {
        val ethTransferGasLimit = BigInteger("21000")
    }
}