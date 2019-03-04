package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.domain.redpacket.*
import io.wexchain.dccchainservice.domain.trustpocket.CheckCodeBean
import io.wexchain.dccchainservice.domain.trustpocket.DepositWalletsBean
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
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

    // 会员查询蟋蟀信息
    @POST("bemember/ss/player/getCricketPlayer")
    fun getCricketPlayer(@Header(HEADER_TOKEN) token: String?): Single<Result<CricketCount>>

    /**
     * 领取红包
     * @param token
     */
    @POST("bemember/redpacket/receiveRedPacket")
    @FormUrlEncoded
    fun pickRedPacket(@Header(HEADER_TOKEN) token: String,
                      @Field("redPacketId") redPacketId: Long): Single<Result<GetPacketBean>>

    /**
     * 查询库存
     * @param token
     */
    @POST("bemember/redpacket/queryStock")
    fun queryStore(@Header(HEADER_TOKEN) token: String): Single<Result<List<QueryStoreBean>>>

    /**
     * 查询邀请信息
     * @param token
     */
    @POST("bemember/redpacket/queryInviteInfo")
    fun queryInviteInfo(@Header(HEADER_TOKEN) token: String): Single<Result<InviteInfoBean>>

    /**
     * 分页查询被邀请者信息
     * @param token
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/redpacket/queryInviteePage")
    @FormUrlEncoded
    fun queryInviteRecord(@Header(HEADER_TOKEN) token: String,
                          @Field("number") number: Long,
                          @Field("size") size: Long
    ): Single<Result<PagedList<InviteRecordBean>>>

    /**
     * 查询红包活动
     * @param token
     */
    @POST("bemember/redpacket/getActivity")
    fun getRedPacketActivity(@Header(HEADER_TOKEN) token: String?): Single<Result<RedPacketActivityBean>>

    /**
     * 领取红包记录
     * @param token
     */
    @POST("bemember/redpacket/queryRedPacketOrderPage")
    fun getRedPacketRecord(@Header(HEADER_TOKEN) token: String): Single<Result<PagedList<GetRecordBean>>>

    /**
     * 获取微信小程序码
     * @param token
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("redpacket/getWXACodeUnlimit")
    @FormUrlEncoded
    fun getRedPacketErCodeFirst(@Field("scene") scene: String): Single<Result<String>>


    /**
     * 查询红包聚合体
     * @param token
     */
    @POST("bemember/redpacket/queryRedPacketBounded")
    fun getRedPacket(@Header(HEADER_TOKEN) token: String): Single<Result<RedPacketBoundBean>>


    /**
     * 查询托管钱包
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/getHostingWallet")
    fun getHostingWallet(@Header(HEADER_TOKEN) token: String): Single<Result<String>>

    /**
     * 发送短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/sendSmsCode")
    fun sendSmsCode(@Header(HEADER_TOKEN) token: String, @Field("mobile") mobile: String): Single<Result<String>>

    /**
     * 验证短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/validateSmsCode")
    fun validateSmsCode(@Header(HEADER_TOKEN) token: String,
                        @Field("mobile") mobile: String,
                        @Field("code") code: String): Single<Result<CheckCodeBean>>

    /**
     * 绑定开通钱包
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/bindHostingWallet")
    fun bindHostingWallet(@Header(HEADER_TOKEN) token: String,
                          @Field("encPassword") encPassword: String,
                          @Field("salt") salt: String): Single<Result<String>>

    /**
     * 查询储值地址
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/getDepositWallets")
    fun getDepositWallets(@Header(HEADER_TOKEN) token: String,
                          @Field("encPassword") encPassword: String,
                          @Field("salt") salt: String): Single<Result<DepositWalletsBean>>

    /**
     * 准备输入支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/prepareInputPwd")
    fun prepareInputPwd(@Header(HEADER_TOKEN) token: String): Single<Result<CheckCodeBean>>

    /**
     * 验证支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/validatePaymentPassword")
    fun validatePaymentPassword(@Header(HEADER_TOKEN) token: String,
                                @Field("encPassword") encPassword: String,
                                @Field("salt") salt: String): Single<Result<ValidatePaymentPasswordBean>>

    /**
     * 密码锁定规则
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/getLockRule")
    fun getLockRule(@Header(HEADER_TOKEN) token: String): Single<Result<String>>

    /**
     * 创建验证密码上下文
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/createPayPwdSecurityContext")
    fun createPayPwdSecurityContext(@Header(HEADER_TOKEN) token: String,
                                    @Field("contextName") contextName: String = "RESET_PAYPWD" //重设支付密码
    ): Single<Result<String>>

    /**
     * 设置支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/initialPaymentPassword")
    fun initialPaymentPassword(@Header(HEADER_TOKEN) token: String,
                               @Field("encPassword") encPassword: String,
                               @Field("salt") salt: String): Single<Result<ValidatePaymentPasswordBean>>

    /**
     * 查询支付密码状态
     * @return UNLOCKED("未锁定"),LOCKED("已锁定"),BLANK("空白");
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/getPaymentPasswordStatus")
    fun getPaymentPasswordStatus(@Header(HEADER_TOKEN) token: String): Single<Result<String>>

    companion object {
        const val HEADER_TOKEN = "x-auth-token"

    }

}
