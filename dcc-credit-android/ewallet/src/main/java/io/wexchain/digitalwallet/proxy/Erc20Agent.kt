package io.wexchain.digitalwallet.proxy

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.Erc20Helper
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
 * Created by sisel on 2018/1/22.
 * ERC20 token agent
 */
open class Erc20Agent(
        val erc20: DigitalCurrency,
        rpcAgent: EthsRpcAgent,
        txAgent: EthsTxAgent
) : EthCurrencyAgent(rpcAgent, txAgent) {

    val contractAddress = erc20.contractAddress!!

    override fun getNonce(address: String): Single<BigInteger> {
        return rpcAgent.getTransactionCount(address, "pending")
    }

    override fun encodeTransfer(nonce: BigInteger, from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String?): String {
        val gas = if (gasLimit < EthereumAgent.ethTransferGasLimit) EthereumAgent.ethTransferGasLimit else gasLimit
        val transferCall = Erc20Helper.transferTx(to, amount)
        return Numeric.toHexString(TransactionEncoder.signMessage(RawTransaction.createTransaction(nonce, gasPrice, gas, contractAddress, transferCall), from))!!
    }

    override fun getGasLimit(ethsTransactionScratch: EthsTransactionScratch): Single<BigInteger> {
        assert(erc20 == ethsTransactionScratch.currency)
        return if (transferGasLimitGot()) {
            Single.just(this.transferGasLimit)
        } else {
            val currency = erc20
            val txAmount = currency.toIntExact(ethsTransactionScratch.amount)
            val txData = Erc20Helper.transferTx(ethsTransactionScratch.to, txAmount)
            val ethJsonTxScratch = EthJsonTxScratch(
                    from = ethsTransactionScratch.from,
                    to = currency.contractAddress!!,
                    value = Numeric.toHexStringWithPrefix(BigInteger.ZERO),
                    data = txData
            )
            return rpcAgent
                    .estimateGas(ethJsonTxScratch)
                    .doOnSuccess {
                        if (!transferGasLimitGot()) {
                            transferGasLimit = it
                        }
                    }
        }
    }

    private lateinit var transferGasLimit: BigInteger

    private fun transferGasLimitGot(): Boolean {
        return this::transferGasLimit.isInitialized
    }

    override fun getBalanceOf(address: String): Single<BigInteger> {
        return rpcAgent.getErc20Balance(contractAddress, address)
    }

}