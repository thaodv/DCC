package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import okhttp3.Response
import retrofit2.http.*

interface MarketingApi {

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065559]
     */
    @GET("marketing/queryActivity")
    fun queryActivity(
            @Query("merchantCode") merchantCode:String?=null
    ):Single<Result<List<MarketingActivity>>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065561]
     */
    @GET("marketing/queryScenario")
    fun queryScenario(
            @Query("activityCode") activityCode:String,
            @Query("address") address:String
    ):Single<Result<List<MarketingActivityScenario>>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065563]
     */
    @POST("marketing/applyRedeemToken")
    @FormUrlEncoded
    fun applyRedeemToken(
            @Field("scenarioCode") scenarioCode:String,
            @Field("address") address:String
    ):Single<Result<String>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065854]
     */
    @GET("bank/list")
    fun getBankList():Single<Result<List<Bank>>>

    @GET("version/checkUpgrade")
    fun checkUpgrade(@Query("version") version:String):Single<Result<CheckUpgrade>>
}