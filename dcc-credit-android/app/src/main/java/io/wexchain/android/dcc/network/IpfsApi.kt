package io.wexchain.android.dcc.network

import io.reactivex.Single
import io.wexchain.dcc.BuildConfig
import io.wexchain.dccchainservice.domain.IpfsVersion
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.digitalwallet.api.domain.EthJsonTxReceipt
import retrofit2.http.*
import java.util.concurrent.atomic.AtomicLong

/**
 *Created by liuyang on 2018/8/15.
 */
interface IpfsApi {

    @POST("contract/1/web3/{business}")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postCall(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>

    @POST("contract/1/web3/{business}")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postSendRawTransaction(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("contract/1/web3/{business}")
    fun getTransactionReceipt(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    @GET("contract/1/getContractAddress/{business}")
    fun getIpfsContractAddress(@Path("business") business: String): Single<Result<String>>

    @GET()
    fun getIpfsVersion(@Url url: String): Single<IpfsVersion>


    companion object {
        const val BASE_URL = BuildConfig.CONTRACT_BASE_URL

        const val IPFS_KEY_HASH = "ipfs_key_hash"
        const val IPFS_METADATA = "ipfs_metadata"

        internal val idAtomic = AtomicLong(0L)
    }
}

fun IpfsApi.sendRawTransaction(business: String, rawTransaction: String): Single<String> {
    return this.postSendRawTransaction(business,
            EthJsonRpcRequestBody(method = "eth_sendRawTransaction",
                    params = listOf(rawTransaction),
                    id = IpfsApi.idAtomic.incrementAndGet()))
            .map { it.result!! }
}


fun IpfsApi.transactionReceipt(business: String, txId: String): Single<EthJsonTxReceipt> {
    return this.getTransactionReceipt(
            business,
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionReceipt",
                    params = kotlin.collections.listOf(txId),
                    id = this.nextId()
            ))
            .flatMap {
                if (it.result != null) Single.just(it.result) else Single.error(IllegalStateException("No receipt yet"))
            }
}

internal fun IpfsApi.nextId(): Long {
    return IpfsApi.idAtomic.incrementAndGet()
}