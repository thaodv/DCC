package io.wexchain.android.dcc.network

import io.reactivex.Single
import io.wexchain.dcc.BuildConfig
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

    /**
     * 获取合约地址
     */
    @GET("contract/1/getContractAddress/{business}")
    fun getIpfsContractAddress(@Path("business") business: String): Single<Result<String>>

    companion object {
        const val BASE_URL = BuildConfig.CONTRACT_BASE_URL

        const val IPFS_KEY_HASH = "ipfs_key_hash"
        const val IPFS_METADATA = "ipfs_metadata"
        const val BSX_DCC_01 = "bsx_01"
        const val BSX_DCC_02 = "bsx_02"
        const val BSX_DCC_03 = "bsx_03"
        const val BSX_DCC_04 = "bsx_04"
        const val BSX_DCC_05 = "bsx_05"
        const val BSX_DCC_06 = "bsx_06"
        const val BSX_DCC_07 = "bsx_07"
        const val BSX_DCC_08 = "bsx_08"
        const val BSX_DCC_09 = "bsx_09"
        const val BSX_DCC_10 = "bsx_10"
        const val BSX_DCC_11 = "bsx_11"
        const val BSX_DCC_12 = "bsx_12"
        const val BSX_DCC_13 = "bsx_13"
        const val BSX_DCC_14 = "bsx_14"
        const val BSX_DCC_15 = "bsx_15"
        const val BSX_DCC_16 = "bsx_16"
        const val BSX_DCC_17 = "bsx_17"
        const val BSX_DCC_18 = "bsx_18"
        const val BSX_DCC_19 = "bsx_19"
        const val BSX_DCC_20 = "bsx_20"

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
                if (it.result != null && it.result!!.logs.isNotEmpty()) Single.just(it.result) else Single.error(IllegalStateException("No receipt yet"))
            }
}

internal fun IpfsApi.nextId(): Long {
    return IpfsApi.idAtomic.incrementAndGet()
}
