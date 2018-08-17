package io.wexchain.android.dcc.network

import io.reactivex.Single
import io.wexchain.dcc.BuildConfig
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.digitalwallet.api.domain.EthJsonTxReceipt
import org.json.JSONArray
import retrofit2.http.*
import java.util.concurrent.atomic.AtomicLong

/**
 *Created by liuyang on 2018/8/15.
 */
interface ContractApi {

    @POST("contract/1/web3/{business}")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postCall(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<Any>
    ): Single<EthJsonRpcResponse<String>>

    @POST("contract/1/web3/{business}")
    @Headers("Content-Type: application/json", "Accept: application/json","parityrpc:1")
    fun postSendRawTransaction(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("Content-Type: application/json", "Accept: application/json","parityrpc:1")
    @POST("contract/1/web3/{business}")
    fun getTransactionCount(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<String>>

    @Headers("Content-Type: application/json", "Accept: application/json","parityrpc:1")
    @POST("contract/1/web3/{business}")
    fun getTransactionReceipt(
            @Path("business") business: String,
            @Body body: EthJsonRpcRequestBody<String>
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    @GET("contract/1/getContractAddress/{business}")
    fun getIpfsContractAddress(@Path("business") business: String): Single<Result<String>>


    companion object {
        private const val InfuraApiToken = cc.sisel.ewallet.BuildConfig.INFURA_API_KEY
        const val BASE_URL = BuildConfig.CONTRACT_BASE_URL

        const val IPFS_KEY_HASH = "ipfs_key_hash"
        const val IPFS_METADATA = "ipfs_metadata"

        internal val idAtomic = AtomicLong(0L)

    }
}

fun ContractApi.sendRawTransaction(business: String, rawTransaction: String): Single<String> {
    return this.postSendRawTransaction(business,
            EthJsonRpcRequestBody(method = "eth_sendRawTransaction",
                    params = listOf(rawTransaction),
                    id = ContractApi.idAtomic.incrementAndGet()))
            .map { it.result!! }
}

fun ContractApi.transactionCount(business: String, address: String, tag: String = "latest"): Single<String> {
    return this.getTransactionCount(
            business,
            EthJsonRpcRequestBody(
                    method = "eth_getTransactionCount",
                    params = listOf(address, tag),
                    id = this.nextId()
            ))
            .map { it.result!! }
}

fun ContractApi.transactionReceipt(business: String, txId: String): Single<EthJsonTxReceipt> {
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

internal fun ContractApi.nextId(): Long {
    return ContractApi.idAtomic.incrementAndGet()
}