package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.Bank
import io.wexchain.dccchainservice.domain.MarketingActivity
import io.wexchain.dccchainservice.domain.MarketingActivityScenario
import io.wexchain.dccchainservice.domain.Result
import retrofit2.http.*

interface MarketingApi {

    @GET("marketing/queryActivity")
    fun queryActivity(
            @Query("merchantCode") merchantCode:String?=null
    ):Single<Result<List<MarketingActivity>>>

    @GET("marketing/queryScenario")
    fun queryScenario(
            @Query("activityCode") activityCode:String,
            @Query("address") address:String
    ):Single<Result<List<MarketingActivityScenario>>>

    @POST("marketing/applyRedeemToken")
    @FormUrlEncoded
    fun applyRedeemToken(
            @Field("scenarioCode") scenarioCode:String,
            @Field("address") address:String
    ):Single<Result<String>>

    @GET("bank/list")
    fun getBankList():Single<Result<List<Bank>>>
}