package io.wexchain.digitalwallet.proxy

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.wexchain.digitalwallet.api.*
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.digitalwallet.api.domain.EthJsonTxInfo
import io.wexchain.digitalwallet.api.domain.EthJsonTxReceipt
import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import io.wexchain.digitalwallet.util.hexToBigIntegerOrThrow
import java.math.BigInteger

/**
 * Created by sisel on 2018/2/7.
 */
interface EthsRpcAgent {
    fun getTransactionCount(address: String, block: String): Single<BigInteger>

    fun getGasPrice(): Single<BigInteger>

    fun checkNode(): Single<String>

    fun estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<BigInteger>

    fun getBalanceOf(address: String): Single<BigInteger>

    fun getErc20Balance(contractAddress: String, address: String): Single<BigInteger>

    /**
     * @return TxId of successfully submitted transaction
     */
    fun sendTransaction(nonce: Single<BigInteger>, encodeTx: (BigInteger) -> String): Single<Pair<BigInteger, String>>

    fun transactionByHash(txId: String): Single<EthJsonTxInfo>
    fun transactionReceipt(txId: String): Single<EthJsonTxReceipt>

    fun getBsxStatus(contractAddress: String): Single<EthJsonRpcResponse<String>>

    fun getBsxSaleInfo(contractAddress: String): Single<EthJsonRpcResponse<String>>

    fun getBsxMinAmountPerHand(contractAddress: String): Single<EthJsonRpcResponse<String>>

    fun getBsxInvestCeilAmount(contractAddress: String): Single<EthJsonRpcResponse<String>>

    fun investedBsxTotalAmount(contractAddress: String): Single<EthJsonRpcResponse<String>>

    fun investedBsxAmountMapping(contractAddress: String, userAddress: String): Single<EthJsonRpcResponse<String>>

    companion object {
        fun by(ethJsonRpcApi: EthJsonRpcApi): EthsRpcAgent {
            val api = ethJsonRpcApi
            return object : EthsRpcAgent {

                override fun investedBsxAmountMapping(contractAddress: String, userAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxAmountMapping(contractAddress, userAddress)
                }

                override fun getBsxStatus(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxStatus(contractAddress)
                }

                override fun getBsxSaleInfo(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxSaleInfo(contractAddress)
                }

                override fun getBsxMinAmountPerHand(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxMinAmountPerHand(contractAddress)
                }

                override fun getBsxInvestCeilAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxInvestCeilAmount(contractAddress)
                }

                override fun investedBsxTotalAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxTotalAmount(contractAddress)
                }

                override fun transactionByHash(txId: String): Single<EthJsonTxInfo> {
                    return api.transactionByHash(txId)
                }

                override fun transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
                    return api.transactionReceipt(txId)
                }

                override fun estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<BigInteger> {
                    return api.estimateGas(ethJsonTxScratch).map { it.hexToBigIntegerOrThrow() }
                }

                override fun getErc20Balance(contractAddress: String, address: String): Single<BigInteger> {
                    return api.getErc20Balance(contractAddress, address)
                }

                override fun getTransactionCount(
                        address: String,
                        block: String
                ): Single<BigInteger> {
                    return api.transactionCount(address, block).map { it.hexToBigIntegerOrThrow() }
                }

                override fun checkNode(): Single<String> {
                    return api.checkNode().map { it }
                }

                override fun getGasPrice(): Single<BigInteger> {
                    return api.gasPrice().map { it.hexToBigIntegerOrThrow() }
                }

                override fun getBalanceOf(address: String): Single<BigInteger> {
                    return api.balanceOf(address).map { it.hexToBigIntegerOrThrow() }
                }

                override fun sendTransaction(
                        nonce: Single<BigInteger>,
                        encodeTx: (BigInteger) -> String
                ): Single<Pair<BigInteger, String>> {
                    return nonce
                            .observeOn(Schedulers.computation())
                            .map {
                                //nonce = transactionCount + 1
                                val nonce = it
                                nonce to encodeTx(nonce)

                            }
                            .observeOn(Schedulers.io())
                            .flatMap { (nonce, rtx) ->
                                api.sendRawTransaction(rtx)
                                        .map { nonce to it }
                            }
                }
            }
        }

        fun by(ethJsonRpcApi: EthJsonRpcApiWithAuth): EthsRpcAgent {
            val api = ethJsonRpcApi
            return object : EthsRpcAgent {
                override fun investedBsxAmountMapping(contractAddress: String, userAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxAmountMapping(contractAddress, userAddress)
                }

                override fun getBsxStatus(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxStatus(contractAddress)
                }

                override fun getBsxSaleInfo(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxSaleInfo(contractAddress)
                }

                override fun getBsxMinAmountPerHand(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxMinAmountPerHand(contractAddress)
                }

                override fun getBsxInvestCeilAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxInvestCeilAmount(contractAddress)
                }

                override fun investedBsxTotalAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxTotalAmount(contractAddress)
                }

                override fun transactionByHash(txId: String): Single<EthJsonTxInfo> {
                    return api.transactionByHash(txId)
                }

                override fun transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
                    return api.transactionReceipt(txId)
                }

                override fun estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<BigInteger> {
                    return api.estimateGas(ethJsonTxScratch).map { it.hexToBigIntegerOrThrow() }
                }

                override fun getErc20Balance(contractAddress: String, address: String): Single<BigInteger> {
                    return api.getErc20Balance(contractAddress, address)
                }

                override fun getTransactionCount(
                        address: String,
                        block: String
                ): Single<BigInteger> {
                    return api.transactionCount(address, block).map { it.hexToBigIntegerOrThrow() }
                }

                override fun checkNode(): Single<String> {
                    return api.checkNode().map { it }
                }

                override fun getGasPrice(): Single<BigInteger> {
                    return api.gasPrice().map { it.hexToBigIntegerOrThrow() }
                }

                override fun getBalanceOf(address: String): Single<BigInteger> {
                    return api.balanceOf(address).map { it.hexToBigIntegerOrThrow() }
                }

                override fun sendTransaction(
                        nonce: Single<BigInteger>,
                        encodeTx: (BigInteger) -> String
                ): Single<Pair<BigInteger, String>> {
                    return nonce
                            .observeOn(Schedulers.computation())
                            .map {
                                encodeTx(it) to it
                            }
                            .observeOn(Schedulers.io())
                            .flatMap {
                                val no = it.second
                                api.sendRawTransaction(it.first).map { no to it }
                            }
                }
            }
        }

        fun by(infuraApi: InfuraApi): EthsRpcAgent {
            val api = infuraApi
            return object : EthsRpcAgent {
                override fun investedBsxAmountMapping(contractAddress: String, userAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxAmountMapping(contractAddress, userAddress)
                }

                override fun getBsxStatus(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxStatus(contractAddress)
                }

                override fun getBsxSaleInfo(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxSaleInfo(contractAddress)
                }

                override fun getBsxMinAmountPerHand(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxMinAmountPerHand(contractAddress)
                }

                override fun getBsxInvestCeilAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.getBsxInvestCeilAmount(contractAddress)
                }

                override fun investedBsxTotalAmount(contractAddress: String): Single<EthJsonRpcResponse<String>> {
                    return api.investedBsxTotalAmount(contractAddress)
                }

                override fun transactionByHash(txId: String): Single<EthJsonTxInfo> {
                    return api.transactionByHash(txId)
                }

                override fun transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
                    return api.transactionReceipt(txId)
                }

                override fun estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<BigInteger> {
                    return api.estimateGas(ethJsonTxScratch).map { it.hexToBigIntegerOrThrow() }
                }

                override fun getErc20Balance(contractAddress: String, address: String): Single<BigInteger> {
                    return api.getErc20Balance(contractAddress, address)
                }

                override fun getTransactionCount(
                        address: String,
                        block: String
                ): Single<BigInteger> {
                    return api.transactionCount(address, block).map { it.hexToBigIntegerOrThrow() }
                }

                override fun getGasPrice(): Single<BigInteger> {
                    return api.getGasPrice().map { it.result!!.hexToBigIntegerOrThrow() }
                }

                override fun checkNode(): Single<String> {
                    return api.checkNode().map { it.toString() }
                }

                override fun getBalanceOf(address: String): Single<BigInteger> {
                    return api.balanceOf(address).map { it.hexToBigIntegerOrThrow() }
                }

                override fun sendTransaction(
                        nonce: Single<BigInteger>,
                        encodeTx: (BigInteger) -> String
                ): Single<Pair<BigInteger, String>> {
                    return nonce
                            .observeOn(Schedulers.computation())
                            .map { encodeTx(it) to it }
                            .observeOn(Schedulers.io())
                            .flatMap {
                                val no = it.second
                                api.sendRawTransaction(it.first).map { no to it }
                            }
                }
            }
        }
    }
}
