package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.domain.redpacket.*
import io.wexchain.dccchainservice.domain.trustpocket.*
import io.wexchain.dccchainservice.util.DateUtil
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal
import java.text.SimpleDateFormat

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
     * 1.查询托管钱包
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/getHostingWallet")
    fun getHostingWallet(@Header(HEADER_TOKEN) token: String): Single<Result<BindHostingWalletBean>>

    /**
     * 2.发送短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/sendSmsCode")
    @FormUrlEncoded
    fun sendSmsCode(@Header(HEADER_TOKEN) token: String, @Field("mobile") mobile: String): Single<Result<String>>

    /**
     * 3.验证短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/validateSmsCode")
    @FormUrlEncoded
    fun validateSmsCode(@Header(HEADER_TOKEN) token: String,
                        @Field("mobile") mobile: String,
                        @Field("code") code: String): Single<Result<CheckCodeBean>>

    /**
     * 4.绑定开通钱包
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/bindHostingWallet")
    @FormUrlEncoded
    fun bindHostingWallet(@Header(HEADER_TOKEN) token: String,
                          @Field("encPassword") encPassword: String,
                          @Field("salt") salt: String): Single<Result<BindHostingWalletBean>>

    /**
     * 5.查询储值地址
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/getDepositWallets")
    @FormUrlEncoded
    fun getDepositWallets(@Header(HEADER_TOKEN) token: String,
                          @Field("encPassword") encPassword: String,
                          @Field("salt") salt: String): Single<Result<List<DepositWalletsBean>>>

    /**
     * 6.准备输入支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/prepareInputPwd")
    fun prepareInputPwd(@Header(HEADER_TOKEN) token: String): Single<Result<CheckCodeBean>>

    /**
     * 7.验证支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/validatePaymentPassword")
    @FormUrlEncoded
    fun validatePaymentPassword(@Header(HEADER_TOKEN) token: String,
                                @Field("encPassword") encPassword: String,
                                @Field("salt") salt: String): Single<Result<ValidatePaymentPasswordBean>>

    /**
     * 8.密码锁定规则
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/getLockRule")
    fun getLockRule(@Header(HEADER_TOKEN) token: String): Single<Result<String>>

    /**
     * 9.创建验证密码上下文
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/createPayPwdSecurityContext")
    @FormUrlEncoded
    fun createPayPwdSecurityContext(@Header(HEADER_TOKEN) token: String,
                                    @Field("contextName") contextName: String = "RESET_PAYPWD" //重设支付密码
    ): Single<Result<String>>

    /**
     * 10.设置支付密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/initialPaymentPassword")
    @FormUrlEncoded
    fun initialPaymentPassword(@Header(HEADER_TOKEN) token: String,
                               @Field("encPassword") encPassword: String,
                               @Field("salt") salt: String): Single<Result<String>>

    /**
     * 11.查询支付密码状态
     * @return UNLOCKED("未锁定"),LOCKED("已锁定"),BLANK("空白");
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/payPwd/getPaymentPasswordStatus")
    fun getPaymentPasswordStatus(@Header(HEADER_TOKEN) token: String): Single<Result<String>>


    /**
     * 12.资产预览
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/asset/getAssetOverview")
    fun getAssetOverview(@Header(HEADER_TOKEN) token: String): Single<Result<GetAssetOverviewBean>>


    /**
     * 13.上架资产
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/asset/listAsset")
    fun listAsset(@Header(HEADER_TOKEN) token: String): Single<Result<List<TrustAssetBean>>>

    /**
     * 14.搜索上架资产
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/asset/searchAssetList")
    @FormUrlEncoded
    fun searchAssetList(@Header(HEADER_TOKEN) token: String,
                        @Field("keyword") keyword: String,
                        @Field("candidateSize") candidateSize: Int = 1): Single<Result<List<TrustAssetBean>>>

    /**
     * 15.换绑手机发送短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/mobileUser/sendSmsCode")
    @FormUrlEncoded
    fun changeSendSmsCode(@Header(HEADER_TOKEN) token: String,
                          @Field("mobile") mobile: String): Single<Result<String>>

    /**
     * 16.验证换绑短信验证码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/mobileUser/validateSmsCode")
    @FormUrlEncoded
    fun changeValidateSmsCode(@Header(HEADER_TOKEN) token: String,
                              @Field("mobile") mobile: String,
                              @Field("code") code: String): Single<Result<String>>

    /**
     * 17.换绑手机
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/mobileUser/updateMobile")
    @FormUrlEncoded
    fun updateMobile(@Header(HEADER_TOKEN) token: String,
                     @Field("mobile") mobile: String,
                     @Field("code") code: String): Single<Result<String>>

    /**
     * 18.查询手机用户
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/mobileUser/getMobileUser")
    fun getMobileUser(@Header(HEADER_TOKEN) token: String): Single<Result<GetMobileUserBean>>

    /**
     * 19.查询充值订单分页
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/queryDepositOrderPage")
    @FormUrlEncoded
    fun queryDepositOrderPage(@Header(HEADER_TOKEN) token: String,
                              @Field("assetCode") assetCode: String,
                              @Field("number") number: Int,
                              @Field("size") size: Int,
                              @Field("startTime") startTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")),
                              @Field("endTime") endTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))): Single<Result<PagedList<QueryDepositOrderPageBean>>>

    /**
     * 20.查询单笔充值订单
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/getDepositOrder")
    @FormUrlEncoded
    fun getDepositOrder(@Header(HEADER_TOKEN) token: String,
                        @Field("id") id: String): Single<Result<QueryDepositOrderPageBean>>

    /**
     * 21.查询取现订单分页
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/queryWithdrawOrderPage")
    @FormUrlEncoded
    fun queryWithdrawOrderPage(@Header(HEADER_TOKEN) token: String,
                               @Field("assetCode") assetCode: String,
                               @Field("number") number: String,
                               @Field("size") size: String,
                               @Field("startTime") startTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")),
                               @Field("endTime") endTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))): Single<Result<PagedList<QueryWithdrawOrderPageBean>>>

    /**
     * 22.查询单笔取现订单
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/getWithdrawOrder")
    @FormUrlEncoded
    fun getWithdrawOrder(@Header(HEADER_TOKEN) token: String,
                         @Field("requestNo") requestNo: String): Single<Result<QueryWithdrawOrderPageBean>>

    /**
     * 23.取现
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/withdraw")
    @FormUrlEncoded
    fun withdraw(@Header(HEADER_TOKEN) token: String,
                 @Field("assetCode") assetCode: String,
                 @Field("amount") amount: BigDecimal,
                 @Field("receiverAddress") receiverAddress: String): Single<Result<WithdrawBean>>

    /**
     * 24.转账
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/transfer")
    @FormUrlEncoded
    fun transfer(@Header(HEADER_TOKEN) token: String,
                 @Field("assetCode") assetCode: String,
                 @Field("amount") amount: BigDecimal,
                 @Field("receiverMobileUserId") receiverMobileUserId: String): Single<Result<TransferBean>>

    /**
     * 24.查询单笔转账
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/getTransferOrder")
    @FormUrlEncoded
    fun getTransferOrder(@Header(HEADER_TOKEN) token: String,
                         @Field("requestNo") requestNo: String): Single<Result<GetTransferOrderBean>>

    /**
     * 25.查询转账分页
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/queryTransferOrderPage")
    @FormUrlEncoded
    fun queryTransferOrderPage(@Header(HEADER_TOKEN) token: String,
                               @Field("assetCode") assetCode: String,
                               @Field("number") number: String,
                               @Field("size") size: String,
                               @Field("startTime") startTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")),
                               @Field("endTime") endTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd")),
                               @Field("out") out: String): Single<Result<PagedList<QueryTransferOrderPageBean>>>

    /**
     * 25.查询转账分页
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/queryOrderPage")
    @FormUrlEncoded
    fun queryOrderPage(@Header(HEADER_TOKEN) token: String,
                       @Field("assetCode") assetCode: String,
                       @Field("number") number: String,
                       @Field("size") size: String,
                       @Field("startTime") startTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")),
                       @Field("endTime") endTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd")),
                       @Field("out") out: String): Single<Result<PagedList<QueryOrderPageBean>>>

    /**
     * 26.查询会员信息和托管id
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/mobileUser/getMemberAndMobileUserInfo")
    @FormUrlEncoded
    fun getMemberAndMobileUserInfo(@Header(HEADER_TOKEN) token: String,
                                   @Field("mobileOrAddress") mobileOrAddress: String): Single<Result<GetMemberAndMobileUserInfoBean>>

    /**
     * 27.查询余额
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/asset/getBalance")
    @FormUrlEncoded
    fun getBalance(@Header(HEADER_TOKEN) token: String,
                   @Field("assetCode") assetCode: String): Single<Result<GetBalanceBean>>

    /**
     * 28.查询单个币种储值地址
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/getDepositWallet")
    @FormUrlEncoded
    fun getDepositWallet(@Header(HEADER_TOKEN) token: String,
                         @Field("assetCode") assetCode: String): Single<Result<DepositWalletsBean>>

    /**
     * 29.查询提现手续费
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/pay/getWithdrawFee")
    @FormUrlEncoded
    fun getWithdrawFee(@Header(HEADER_TOKEN) token: String,
                       @Field("assetCode") assetCode: String,
                       @Field("receiverAddress") receiverAddress: String,
                       @Field("amount") amount: String): Single<Result<GetWithdrawFeeBean>>

    /**
     * 30.查询最小起提金额
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/asset/getAssetConfigMinAmountByCode")
    @FormUrlEncoded
    fun getAssetConfigMinAmountByCode(@Header(HEADER_TOKEN) token: String,
                                      @Field("assetCode") assetCode: String,
                                      @Field("type") type: String = "WITHDRAW_MIN_AMT"): Single<Result<String>>

    /**
     * 31.汇率查询
     */
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("bemember/wallet/quote/getUsdtCnyQuote")
    fun getUsdtCnyQuote(@Header(HEADER_TOKEN) token: String): Single<Result<String>>


    companion object {
        const val HEADER_TOKEN = "x-auth-token"

    }

}
