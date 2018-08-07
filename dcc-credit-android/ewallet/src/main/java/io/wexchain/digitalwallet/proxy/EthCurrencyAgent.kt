package io.wexchain.digitalwallet.proxy

import io.reactivex.Single
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.api.domain.EthJsonTxInfo
import io.wexchain.digitalwallet.api.domain.EthJsonTxReceipt
import org.web3j.crypto.Credentials
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/22.
 * Ethereum standard Currency and Tokens Access Agent
 */
abstract class EthCurrencyAgent(
        protected val rpcAgent: EthsRpcAgent,
        protected val txAgent: EthsTxAgent
) {
    /**
     * new transaction nonce
     */
    abstract fun getNonce(address: String): Single<BigInteger>

    /**
     * encode and sign rawTransaction
     */
    abstract fun encodeTransfer(nonce: BigInteger, from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String?): String

    open fun getGasPrice(): Single<BigInteger> {
        return rpcAgent.getGasPrice()
    }

    abstract fun getGasLimit(ethsTransactionScratch: EthsTransactionScratch): Single<BigInteger>

    abstract fun getBalanceOf(address: String): Single<BigInteger>

    /**
     * @return TxId of successfully submitted transaction 转账接口
     */
    fun sendTransferTransaction(from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String? = null): Single<Pair<BigInteger,String>> {
        return rpcAgent.sendTransaction(getNonce(from.address), { nonce -> encodeTransfer(nonce, from, to, amount, gasPrice, gasLimit, remarks) })
    }
   /* *//**
     * @return TxId of successfully submitted transaction 编辑撤销转账接口
     */
    fun editTransferTransaction(nonceo: Single<BigInteger>,from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String? = null): Single<Pair<BigInteger,String>> {
        return rpcAgent.sendTransaction(nonceo, { nonce -> encodeTransfer(nonce, from, to, amount, gasPrice, gasLimit, remarks) })
    }
    /**
     * @param address the address queried
     * @param start block
     * @param end
     */
    fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>> {
        return txAgent.listTransactionsOf(address, start, end)
    }

    fun transactionByHash(txId: String): Single<EthJsonTxInfo> {
        return rpcAgent.transactionByHash(txId)
    }

    fun transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
        return rpcAgent.transactionReceipt(txId)
    }

}
