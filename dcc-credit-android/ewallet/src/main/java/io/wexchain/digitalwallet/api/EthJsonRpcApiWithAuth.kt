package io.wexchain.digitalwallet.api

import io.reactivex.Single
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.*
import org.web3j.utils.Numeric
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Altered json rpc interface
 * add fix header
 */
interface EthJsonRpcApiWithAuth {

    @Headers("parityrpc:1")
    @POST("./")
    fun getBalance(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("parityrpc:1")
    @POST("./")
    fun getTransactionCount(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("Content-Type: application/json", "Accept: application/json", "parityrpc:1")
    @POST("./")
    fun checkNode(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("parityrpc:1")
    @POST("./")
    fun getGasPrice(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("parityrpc:1")
    @POST("./")
    fun getTransactionReceipt(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    @Headers("parityrpc:1")
    @POST("./")
    fun getTransactionByHash(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxInfo>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json", "parityrpc:1")
    fun postSendRawTransaction(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json", "parityrpc:1")
    fun postEstimateGas(
            @Body body: EthJsonRpcRequestBody<EthJsonTxScratch>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json", "parityrpc:1")
    fun postCall(
            @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>

    companion object {

        const val RPC_URL = "https://ethrpc.wexfin.com:58545/"

        fun juzixErc20RpcUrl(gateWayUrl: String, symbol: String): String {
            return "${gateWayUrl}erc20/$symbol/1/web3/"
        }

        internal val idAtomic = AtomicLong(0L)
    }
}

internal fun EthJsonRpcApiWithAuth.nextId(): Long {
    return EthJsonRpcApiWithAuth.idAtomic.incrementAndGet()
}

fun EthJsonRpcApiWithAuth.checkNode(): Single<String> {
    return this.checkNode(
            EthJsonRpcRequestBody(
                    method = "net_version",
                    params = kotlin.collections.listOf(),
                    id = 1
            ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.gasPrice(): Single<String> {
    return this.getGasPrice(
            EthJsonRpcRequestBody(
                    method = "eth_gasPrice",
                    params = kotlin.collections.listOf(),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.balanceOf(address: String, tag: String = "latest"): Single<String> {
    return this.getBalance(
            EthJsonRpcRequestBody(
                    method = "eth_getBalance",
                    params = kotlin.collections.listOf(address, tag),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.transactionCount(address: String, tag: String = "latest"): Single<String> {
    return this.getTransactionCount(
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionCount",
                    params = kotlin.collections.listOf(address, tag),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
    return this.getTransactionReceipt(
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionReceipt",
                    params = kotlin.collections.listOf(txId),
                    id = this.nextId()
            ))
            .flatMap {
                if (it.result != null) Single.just(it.result) else Single.error(IllegalStateException("No receipt yet"))
            }
}

fun EthJsonRpcApiWithAuth.transactionByHash(txId: String): Single<EthJsonTxInfo> {
    return this.getTransactionByHash(
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionByHash",
                    params = kotlin.collections.listOf(txId),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.sendRawTransaction(rawTransaction: String): Single<String> {
    return this.postSendRawTransaction(EthJsonRpcRequestBody(
            method = "eth_sendRawTransaction",
            params = listOf(rawTransaction),
            id = this.nextId()
    ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<String> {
    return this.postEstimateGas(EthJsonRpcRequestBody(
            method = "eth_estimateGas",
            params = listOf(ethJsonTxScratch),
            id = this.nextId()
    ))
            .map { it.result!! }
}

fun EthJsonRpcApiWithAuth.getErc20Balance(contractAddress: String, address: String, tag: String = "latest"): Single<BigInteger> {
    val call = Erc20Helper.getBalanceCall(contractAddress, address)
    return this.postCall(EthJsonRpcRequestBody(
            method = "eth_call",
            params = listOf(call, tag),
            id = this.nextId()
    )).map {
        if (it.result.equals("0x", true))
            BigInteger.ZERO
        else
            Numeric.toBigInt(it.result!!)
    }
}
