package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import retrofit2.Response
import retrofit2.http.*

interface MarketingApi {

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065559]
     */
    @GET("marketing/queryActivity")
    fun queryActivity(
            @Query("merchantCode") merchantCode: String? = null
    ): Single<Result<List<MarketingActivity>>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065561]
     */
    @GET("marketing/queryScenario")
    fun queryScenario(
            @Query("activityCode") activityCode: String,
            @Query("address") address: String
    ): Single<Result<List<MarketingActivityScenario>>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065563]
     */
    @POST("marketing/applyRedeemToken")
    @FormUrlEncoded
    fun applyRedeemToken(
            @Field("scenarioCode") scenarioCode: String,
            @Field("address") address: String
    ): Single<Result<String>>

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065854]
     */
    @GET("bank/list")
    fun getBankList(): Single<Result<List<Bank>>>

    @GET("version/checkUpgrade")
    fun checkUpgrade(@Query("version") version: String, @Query("platform") platform: String = "ANDROID"): Single<Result<CheckUpgrade>>

    //登录Nonce
    @GET("auth/getNonce2")
    fun getNonce2(): Single<Result<String>>

    //bemember登录
    @POST("bemember/login")
    @FormUrlEncoded
    fun login(
            @Field("address") address: String,
            @Field("nonce") nonce: String,
            @Field("signature") signature: String
    ): Single<Response<Result<UserInfo>>>

    //绑定微信
    @POST("bemember/bound/wechat")
    @FormUrlEncoded
    fun bound(
            @Header(HEADER_TOKEN) token: String?,
            @Field("address") address: String,
            @Field("code") code: String
    ): Single<Result<String>>

    //任务列表
    @POST("bemember/ss/task/taskList")
    fun getTaskList(@Header(HEADER_TOKEN) token: String?): Single<Result<List<TaskList>>>

    //完成任务
    @POST("bemember/ss/task/completeTask")
    @FormUrlEncoded
    fun completeTask(
            @Header(HEADER_TOKEN) token: String?,
            @Field("scenarioCode") scenarioCode: String
    ): Single<Result<ChangeOrder>>

    //每日签到
    @POST("bemember/ss/attendence/apply")
    fun apply(@Header(HEADER_TOKEN) token: String?): Single<Result<WeekRecord>>

    //查询周签到历史
    @POST("bemember/ss/attendence/currentWeekRecord")
    fun currentWeekRecord(@Header(HEADER_TOKEN) token: String?): Single<Result<List<WeekRecord>>>

    //查询当天签到历史
    @POST("bemember/ss/attendence/queryTodayRecord")
    fun queryTodayRecord(@Header(HEADER_TOKEN) token: String?): Single<Result<WeekRecord>>

    // 查询玩家阳光值余额
    @POST("bemember/ss/player/balance")
    fun balance(@Header(HEADER_TOKEN) token: String?): Single<Result<WeekRecord>>

    // 查询玩家阳光值变更记录
    @POST("bemember/ss/score/query")
    @FormUrlEncoded
    fun changeOrder(@Header(HEADER_TOKEN) token: String?,
                    @Field("number") number: Int,
                    @Field("size") size: Int): Single<Result<PagedList<ChangeOrder>>>

    // 小程序获取最后一次对战
    @POST("ss/duel/getLastDuel")
    fun getLastDuel(): Single<Result<ChangeOrder>>

    // 查询是否有可领取花朵
    @POST("bemember/ss/flower/queryFlower")
    @FormUrlEncoded
    fun queryFlower(@Header(HEADER_TOKEN) token: String?,
                    @Field("ownerId") ownerId: Int): Single<Result<Any>>


    companion object {
        const val HEADER_TOKEN = "x-auth-token"
    }

}
