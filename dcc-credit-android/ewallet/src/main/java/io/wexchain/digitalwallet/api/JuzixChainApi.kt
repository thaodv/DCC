package io.wexchain.digitalwallet.api

import cc.sisel.ewallet.BuildConfig
import io.reactivex.Single
import io.wexchain.wexchainservice.domain.Result
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by sisel on 2018/3/22.
 */
interface JuzixChainApi {
    @GET("getFeeRate")
    fun getFeeRate():Single<Result<String>>

    @POST("getExpectedFee")
    fun getExpectedFee(
            @Field("sender") sender:String,
            // uint256 literal
            @Field("amount") amount:String
    ):Single<Result<String>>

    @GET("getContractAddress")
    fun getContractAddress():Single<Result<String>>

    companion object {
        fun urlOf(symbol:String): String {
            return "${BuildConfig.JUZIX_CHAIN_RESTFUL}erc20/$symbol/1/"
        }
    }
}