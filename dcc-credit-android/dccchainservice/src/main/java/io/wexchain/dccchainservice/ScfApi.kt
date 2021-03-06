package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.util.DateUtil
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Api doc see below
 * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60065557]
 */
interface ScfApi {
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("login")
    @FormUrlEncoded
    fun login(
            @Field("nonce") nonce: String,//否	活动code
            @Field("address") address: String,//否	钱包地址
            @Field("signature") sign: String,//否	签名
            @Field("username") username: String,//否	用户名，值为钱包地址
            @Field("password") password: String? = null    //是	传空
    ): Single<Response<Result<String>>>

    @GET("auth/getNonce")
    fun getNonce(): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("loan/product/queryByLenderCode")
    fun queryLoanProductsByLenderCode(
            @Field("lenderCode") lenderCode: String? = null
    ): Single<Result<List<LoanProduct>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("loan/product/getById")
    @FormUrlEncoded
    fun queryLoanProductById(
            @Field("id") id: Long
    ): Single<Result<LoanProduct>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/queryOrders")
    @FormUrlEncoded
    fun queryOrders(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            /**
             * start @ 0
             */
            @Field("number") number: Long,
            @Field("size") size: Long
    ): Single<Result<PagedList<LoanRecord>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/queryOrderPage")
    @FormUrlEncoded
    fun queryOrderPage(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            /**
             * start @ 0
             */
            @Field("number") number: Long,
            @Field("size") size: Long
    ): Single<Result<PagedList<LoanRecordSummary>>>

    @POST("secure/loan/getLastOrder")
    fun getLastOrder(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<LoanChainOrder>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/getByChainOrderId")
    @FormUrlEncoded
    fun getLoanRecordById(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("chainOrderId") chainOrderId: Long
    ): Single<Result<LoanRecord>>

    @POST("secure/loan/apply")
    @Multipart
    fun applyLoanCredit(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Part("orderId") orderId: Long,//否	链上订单id
            @Part("loanProductId") loanProductId: Long,//否	借款产品id
            @Part("borrowName") borrowName: String,//否	借款人真实姓名
            @Part("borrowAmount") borrowAmount: String, //    否	借款金额（11，4）
            @Part("borrowDuration") borrowDuration: Int,//否	借款期限
            @Part("durationUnit") durationUnit: String,//否	借款期限单位（YEAR，MONTH，DAY）
            @Part("certNo") certNo: String,//否	身份证件号
            @Part("mobile") mobile: String,//否	借款人手机号（手机认证）
            @Part("bankCard") bankCard: String,//否	银行卡号
            @Part("bankMobile") bankMobile: String,//否	银行卡预留绑定手机号
            @Part("applyDate") applyDate: Long,//否	申请时间（时间戳）
            @Part("communicationLog") communicationLog: String,
            @Part("version") version: String,
            @Part personalPhoto: MultipartBody.Part,//活体照片
            @Part frontPhoto: MultipartBody.Part,//	身份证正面
            @Part backPhoto: MultipartBody.Part//	身份证反面

    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/getRepaymentBill")
    @FormUrlEncoded
    fun getRepaymentBill(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("chainOrderId") chainOrderId: Long//否	链上订单id
    ): Single<Result<LoanRepaymentBill>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/confirmRepayment")
    @FormUrlEncoded
    fun confirmRepayment(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("chainOrderId") chainOrderId: Long//否	链上订单id
    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("loan/getLoanInterest")
    @FormUrlEncoded
    fun getLoanInterest(
            @Field("productId") productId: Long,
            @Field("amount") amount: String,
            @Field("loanPeriodValue") loanPeriodValue: Int
    ): Single<Result<BigDecimal>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/cancel")
    @FormUrlEncoded
    fun cancelLoan(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("chainOrderId") orderId: Long//否	链上订单id
    ): Single<Result<String>>

    @GET("secure/loan/download/agreement")
    fun loanAgreement(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Query("chainOrderId") chainOrderId: Long//否	链上订单id
    ): Single<ResponseBody>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/queryReport")
    @FormUrlEncoded
    fun queryLoanReport(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("version") version: String
    ): Single<Result<List<LoanReport>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("member/register")
    @FormUrlEncoded
    fun scfRegister(
            @Field("nonce") nonce: String,//否	活动code
            @Field("address") address: String,//否	钱包地址
            @Field("signature") sign: String,//否	签名
            @Field("loginName") loginName: String,//否	用户名，值为钱包地址
            @Field("inviteCode") inviteCode: String?
    ): Single<Response<Result<String>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("member/getByIdentity")
    @FormUrlEncoded
    fun getScfMemberInfo(
            @Field("nonce") nonce: String,//否	活动code
            @Field("address") address: String,//否	钱包地址
            @Field("signature") sign: String//否	签名
    ): Single<Result<ScfAccountInfo>>

    @POST("secure/member/queryByInvited")
    fun invitedList(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<List<ScfAccountInfo>>>

    @POST("secure/marketing/queryBonus")
    fun queryBonus(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<List<RedeemToken>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/applyBonus")
    @FormUrlEncoded
    fun applyBonus(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("redeemTokenId") redeemTokenId: Long
    ): Single<Result<RedeemToken>>

    /**
     * @param number start @ 0
     */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/eco/queryBonus")
    @FormUrlEncoded
    fun queryEcoBonus(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("number") number: Long,
            @Field("size") size: Long = 10L
    ): Single<Result<PagedList<EcoBonus>>>

    @POST("secure/marketing/eco/getTotalBonus")
    fun getTotalEcoBonus(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<BigInteger>>

    @POST("secure/marketing/eco/getYesterdayBonus")
    fun getYesterdayEcoBonus(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<EcoDayIncome>>

    @POST("marketing/eco/queryBonusRule")
    fun queryBonusRule(): Single<Result<List<BonusRule>>>

    /* mining reward */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/candy/queryList")
    @FormUrlEncoded
    fun queryMineCandyList(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("boxCode") boxCode: String = "BITEXPRESS_MINING_CANDY_BOX"//所有环境固定
    ): Single<Result<List<MineCandy>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/candy/pick")
    @FormUrlEncoded
    fun pickMineCandy(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("candyId") candyId: Long
    ): Single<Result<MineCandy>>

    @POST("marketing/mining/queryRule")
    fun queryMinePtRule(): Single<Result<List<BonusRule>>>

    @POST("secure/marketing/mining/queryContributionScore")
    fun queryMineContributionScore(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<BigDecimal>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/mining/queryRecord")
    @FormUrlEncoded
    fun queryMineContributionRecord(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            /**
             * start @ 0
             */
            @Field("number") number: Long,
            @Field("size") size: Long = 20L
    ): Single<Result<PagedList<MineContributionRecord>>>


    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/marketing/mining/queryBonus")
    @FormUrlEncoded
    fun queryMineRewardRecords(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            /**
             * start @ 0
             */
            @Field("number") number: Long,
            @Field("size") size: Long = 20L
    ): Single<Result<PagedList<EcoBonus>>>

    @POST("secure/marketing/mining/getTotalBonus")
    fun queryMineRewardTotalAmount(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<BigInteger>>

    @POST("secure/marketing/mining/signIn")
    fun signIn(
            @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/forex/queryForexOrderPage")
    @FormUrlEncoded
    fun getChainExchangeList(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("startTime") startTime: String,
            @Field("endTime") endTime: String,
            // start @ 1 default 1
            @Field("number") number: Long,
            // default 20
            @Field("size") size: Long,
            @Field("originAssetCodeList") originAssetCodeList: String = "DCC,DCC_JUZIX"
    ): Single<Result<PagedList<AccrossTransRecord>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/forex/queryForexStatisticsInfo")
    @FormUrlEncoded
    fun queryExchangeAmount(@Header(ScfApi.HEADER_TOKEN) token: String?,
                            @Field("startTime") startTime: String = DateUtil.getCurrentMonday(),
                            @Field("endTime") endTime: String = DateUtil.getCurrentSunday(),
                            @Field("originAssetCodeList") originAssetCodeList: String = "DCC,DCC_JUZIX"
    ): Single<Result<List<ExchangeAmount>>>


    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/forex/getForexOrder")
    @FormUrlEncoded
    fun getChainExchangeDetail(@Header(ScfApi.HEADER_TOKEN) token: String?,
                               @Field("orderId") orderId: String
    ): Single<Result<AccrossTransDetail>>

    /**
     * assetCode=DCC ：公链转私链 assetCode=DCC_JUZIX：私链转公链
     */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/forex/getForexConfig")
    @FormUrlEncoded
    fun queryExchangeCondition(@Header(ScfApi.HEADER_TOKEN) token: String?,
                               @Field("assetCode") assetCode: String = "DCC_JUZIX"
    ): Single<Result<ExchangeCondition>>

    /**
     * 获取币生息市场
     */
    @GET("bsx/getActiveList")
    fun getBsxMarketList(): Single<Result<List<BsxMarketBean>>>

    /**
     * 获取用户持仓列表
     */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @GET("bsx/getPositionList")
    fun getBsxHoldingList(@Query("userAddress") userAddress: String): Single<Result<List<BsxHoldingBean>>>

    /**
     * 获取用户投资统计
     */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @GET("bsx/getPositionSum")
    fun getHoldingSum(@Query("userAddress") userAddress: String): Single<Result<BsxHoldingSumBean>>

    //查询借款最大额度
    @POST("secure/tn_loan/getMaximumAmount")
    fun getMaximumAmount(@Header(ScfApi.HEADER_TOKEN) token: String?): Single<Result<String>>

    //创建新现金贷订单
    @POST("secure/tn_loan/createLoanOrder")
    fun createLoanOrder(@Header(ScfApi.HEADER_TOKEN) token: String?): Single<Result<TnLoanOrder>>

    //查询最后一笔创建的订单
    @POST("secure/tn_loan/getLastOrder")
    fun getTnLastOrder(@Header(ScfApi.HEADER_TOKEN) token: String?): Single<Result<TnLoanOrder>>

    //申请现金贷额度
    @POST("secure/tn_loan/apply")
    @Multipart
    fun tnApply(@Header(ScfApi.HEADER_TOKEN) token: String?,
                @Part("data") data: String,
                @Part idCardFrontPic: MultipartBody.Part,
                @Part idCardBackPic: MultipartBody.Part,
                @Part facePic: MultipartBody.Part
    ): Single<Result<TnLoanOrder>>

    //获取额度审核结果
    @POST("secure/tn_loan/getAuditResult")
    @FormUrlEncoded
    fun getAuditResult(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: String
    ): Single<Result<TnLoanOrder>>

    //获取借款试算信息
    @POST("secure/tn_loan/getLoanCalculationInfo")
    @FormUrlEncoded
    fun getLoanCalculationInfo(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: String
    ): Single<Result<TnLoanOrder>>

    //绑卡
    @POST("bank/tn_loan/bindingBankCard")
    @FormUrlEncoded
    fun bindingBankCard(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("bankCode") bankCode: String,
            @Field("bankCardNo") bankCardNo: String,
            @Field("bankCardMobile") bankCardMobile: String,
            @Field("verifyCode") verifyCode: String
    ): Single<Result<String>>

    //查询用户已绑定银行卡
    @POST("bank/tn_loan/getBindingBankCard")
    fun getBindingBankCard(): Single<Result<BankInfo>>

    //查询银行列表
    @POST("bank/tn_loan/queryBankInfo")
    fun queryBankInfo(): Single<Result<List<BankInfo>>>

    //查询同牛借贷订单详情
    @POST("secure/tn_loan/getTNLoanOrderDetail")
    @FormUrlEncoded
    fun getTNLoanOrderDetail(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: String
    ): Single<Result<TnLoanOrder>>

    //查询还款计划
    @POST("secure/tn_loan/getTNRepayPlan")
    @FormUrlEncoded
    fun getTNRepayPlan(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: Int
    ): Single<Result<TnLoanOrder>>

    //分页查询同牛借贷订单
    @POST("secure/tn_loan/queryOrderPage")
    @FormUrlEncoded
    fun queryOrderPage(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("number") number: Int,
            @Field("size") size: Int = 20
    ): Single<Result<PagedList<TnLoanOrder>>>

    //还款
    @POST("secure/tn_loan/repay")
    @FormUrlEncoded
    fun repay(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: Long,
            @Field("verifyCode") verifyCode: Long
    ): Single<Result<TnLoanOrder>>

    //确认借款
    @POST("secure/tn_loan/confirmLoan")
    @FormUrlEncoded
    fun confirmLoan(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: Long,
            @Field("chainOrderId") chainOrderId: Long,
            @Field("useDcc") useDcc: Boolean,
            @Field("dccAmount") dccAmount: BigDecimal
    ): Single<Result<TnLoanOrder>>

    //手机号认证校验
    @POST("secure/tn_loan/mobileNumber")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @FormUrlEncoded
    fun mobileNumber(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("mobileNumber") mobileNumber: String,
            @Field("idNo") idNo: String,
            @Field("username") username: String,
            @Field("nonce") nonce: String
    ): Single<Result<String>>

    //获取借款合同
    @POST("secure/tn_loan/getLoanContract")
    @FormUrlEncoded
    fun getLoanContract(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("id") id: String
    ): Single<Result<TnLoanOrder>>

    companion object {
        const val HEADER_TOKEN = "x-auth-token"
    }

}
