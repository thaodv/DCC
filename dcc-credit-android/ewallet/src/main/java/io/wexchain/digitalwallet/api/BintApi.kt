package io.wexchain.digitalwallet.api

import cc.sisel.ewallet.BuildConfig
import io.reactivex.Single
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.BintApi.Companion.encodeJsonParamArray
import io.wexchain.digitalwallet.api.domain.*
import org.json.JSONArray
import org.web3j.utils.Numeric
import retrofit2.http.*
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by sisel on 2018/1/17.
 * api proxy provided by Infura.io
 * [infura](https://infura.docs.apiary.io/#reference)
 * [eth-json-rpc](https://github.com/ethereum/wiki/wiki/JSON-RPC)
 *
 * base url : https://api.infura.io/v1/jsonrpc/{network}/
 */
interface BintApi  {

    @POST( postUrl)
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postStatus(
        @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>


    @GET("eth_getBalance")
    fun getBalance(
            @Query("params") params: String,
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<String>>

    @GET("status")
    fun getStatus(
        @Query("params") params: String,
        @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<String>>

    @GET("eth_getTransactionCount")
    fun getTransactionCount(
            @Query("params") params: String,
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<String>>

    @GET("eth_gasPrice")
    fun getGasPrice(
            @Query("params") params: String = encodeJsonParamArray(),
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<String>>

    @GET("/")
    fun checkNode(
            @Query("params") params: String = encodeJsonParamArray(),
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<String>>

    @GET("eth_getTransactionReceipt")
    fun getTransactionReceipt(
            @Query("params") params: String,
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    @GET("eth_getTransactionByHash")
    fun getTransactionByHash(
            @Query("params") params: String,
            @Query("token") token: String = InfuraApiToken
    ): Single<EthJsonRpcResponse<EthJsonTxInfo>>

    @POST(postUrl)
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postSendRawTransaction(
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @POST(postUrl)
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postEstimateGas(
            @Body body: EthJsonRpcRequestBody<EthJsonTxScratch>
    ): Single<EthJsonRpcResponse<String>>

    @POST(postUrl)
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postCall(
            @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>

    companion object {
        private const val InfuraApiToken = BuildConfig.INFURA_API_KEY

        val contract = "0x92319baa1648ef13ce86df30a8e31b0768b482b7"

       // private const val postUrl = "https://${BuildConfig.ETH_INFURA_NETWORK}.infura.io/$InfuraApiToken"
        private const val postUrl = "http://10.65.209.13:9511/passport-gateway/restful/contract/1/web3/bsx_01"
        /**
         * base url for restful get
         */
        const val getUrl = "https://api.infura.io/v1/jsonrpc/${BuildConfig.ETH_INFURA_NETWORK}/"
        //    const val getUrl = "http://10.65.209.13:9511/passport-gateway/restful/contract/1/web3/bsx_01/"

        internal val idAtomic = AtomicLong(0L)

        internal fun encodeJsonParamArray(vararg params: String): String {
            return JSONArray().apply {
                params.forEach {
                    this.put(it)
                }
            }.toString()
        }
    }
}

fun BintApi.biOrderStatus(address: String, tag: String = "latest"): Single<String> {
    return this.getStatus(encodeJsonParamArray(address, tag))
        .map { it.result!! }
}

fun BintApi.balanceOf(address: String, tag: String = "latest"): Single<String> {
    return this.getBalance(encodeJsonParamArray(address, tag))
            .map { it.result!! }
}

fun BintApi.transactionCount(address: String, tag: String = "latest"): Single<String> {
    return this.getTransactionCount(encodeJsonParamArray(address, tag))
            .map { it.result!! }
}

fun BintApi.transactionReceipt(txId: String): Single<EthJsonTxReceipt> {
    return this.getTransactionReceipt(encodeJsonParamArray(txId))
            .flatMap {
                if (it.result != null) Single.just(it.result) else Single.error(IllegalStateException("No receipt yet"))
            }
}

fun BintApi.transactionByHash(txId: String): Single<EthJsonTxInfo> {
    return this.getTransactionByHash(encodeJsonParamArray(txId))
            .map { it.result!! }
}

fun BintApi.sendRawTransaction(rawTransaction: String): Single<String> {
    return this.postSendRawTransaction(EthJsonRpcRequestBody(method = "eth_sendRawTransaction", params = listOf(rawTransaction), id = BintApi.idAtomic.incrementAndGet()))
            .map { it.result!! }
}

fun BintApi.estimateGas(ethJsonTxScratch: EthJsonTxScratch): Single<String> {
    return this.postEstimateGas(EthJsonRpcRequestBody(method = "eth_estimateGas", params = listOf(ethJsonTxScratch), id = BintApi.idAtomic.incrementAndGet()))
            .map { it.result!! }
}

fun BintApi.getErc20Balance(contractAddress: String, address: String, tag: String = "latest"): Single<BigInteger> {
    val call = Erc20Helper.getBalanceCall(contractAddress, address)
    return this.postCall(EthJsonRpcRequestBody(
            method = "eth_call",
            params = listOf(call, tag),
            id = BintApi.idAtomic.incrementAndGet()
    )).map { if (it.result.equals("0x", true)) BigInteger.ZERO else Numeric.toBigInt(it.result!!) }
}
