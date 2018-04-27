package io.wexchain.digitalwallet.api

import android.support.annotation.MainThread
import cc.sisel.ewallet.BuildConfig
import io.reactivex.Single
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.api.domain.*
import io.wexchain.digitalwallet.proxy.EthsRpcAgent
import org.web3j.crypto.Credentials
import org.web3j.utils.Numeric
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by sisel on 2018/2/2.
 */
interface EthJsonRpcApi {

    @POST("./")
    fun getBalance(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    fun getTransactionCount(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    fun getGasPrice(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    fun getTransactionReceipt(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    @POST("./")
    fun getTransactionByHash(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxInfo>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postSendRawTransaction(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postEstimateGas(
            @Body body: EthJsonRpcRequestBody<EthJsonTxScratch>
    ): Single<EthJsonRpcResponse<String>>

    @POST("./")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postCall(
            @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>

    companion object {

        const val PUBLIC_RPC_URL = BuildConfig.PUBLIC_CHAIN_RPC

        fun juzixErc20RpcUrl(symbol:String): String {
            return "${BuildConfig.JUZIX_CHAIN_RESTFUL}erc20/$symbol/1/web3/"
        }

        internal val ids = ConcurrentHashMap<EthJsonRpcApi, AtomicLong>()

        @MainThread
        fun prepareId(ethJsonRpcApi: EthJsonRpcApi) {
            ids[ethJsonRpcApi] = AtomicLong()
        }
    }
}

fun EthJsonRpcApi.getPrepared(): EthJsonRpcApi {
    EthJsonRpcApi.prepareId(this)
    return this
}

internal fun EthJsonRpcApi.nextId(): Long {
    return EthJsonRpcApi.ids[this]!!.incrementAndGet()
}

fun EthJsonRpcApi.gasPrice(): Single<String> {
    return this.getGasPrice(
            EthJsonRpcRequestBody(
                    method = "eth_gasPrice",
                    params = kotlin.collections.listOf(),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApi.balanceOf(address: String, tag: String = "latest"): Single<String> {
    return this.getBalance(
            EthJsonRpcRequestBody(
                    method = "eth_getBalance",
                    params = kotlin.collections.listOf(address, tag),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApi.transactionCount(address: String, tag: String = "latest"): Single<String> {
    return this.getTransactionCount(
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionCount",
                    params = kotlin.collections.listOf(address, tag),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApi.transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
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

fun EthJsonRpcApi.transactionByHash(txId: String): Single<EthJsonTxInfo> {
    return this.getTransactionByHash(
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionByHash",
                    params = kotlin.collections.listOf(txId),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun EthJsonRpcApi.sendRawTransaction(rawTransaction: String): Single<String> {
    return this.postSendRawTransaction(EthJsonRpcRequestBody(
            method = "eth_sendRawTransaction",
            params = listOf(rawTransaction),
            id = this.nextId()
    ))
            .map { it.result!! }
}

fun EthJsonRpcApi.estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<String> {
    return this.postEstimateGas(EthJsonRpcRequestBody(
            method = "eth_estimateGas",
            params = listOf(ethJsonTxScratch),
            id = this.nextId()
    ))
            .map { it.result!! }
}

fun EthJsonRpcApi.getErc20Balance(contractAddress: String, address: String, tag: String = "latest"): Single<BigInteger> {
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