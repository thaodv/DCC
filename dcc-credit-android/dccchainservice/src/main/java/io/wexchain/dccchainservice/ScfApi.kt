package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
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

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("secure/loan/apply")
    @FormUrlEncoded
    fun applyLoanCredit(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
            @Field("orderId") orderId: Long,//否	链上订单id
            @Field("loanProductId") loanProductId: Long,//否	借款产品id
            @Field("borrowName") borrowName: String,//否	借款人真实姓名
            @Field("borrowAmount") borrowAmount: String, //    否	借款金额（11，4）
            @Field("borrowDuration") borrowDuration: Int,//否	借款期限
            @Field("durationUnit") durationUnit: String,//否	借款期限单位（YEAR，MONTH，DAY）
            @Field("certNo") certNo: String,//否	身份证件号
            @Field("mobile") mobile: String,//否	借款人手机号（手机认证）
            @Field("bankCard") bankCard: String,//否	银行卡号
            @Field("bankMobile") bankMobile: String,//否	银行卡预留绑定手机号
            @Field("applyDate") applyDate: Long,//否	申请时间（时间戳）
            @Field("personalPhoto") personalPhoto: String,//活体照片 urlEncode(base64(pic))
            @Field("frontPhoto") frontPhoto: String,//	身份证正面 urlEncode(base64(pic))
            @Field("backPhoto") backPhoto: String,//	身份证反面 urlEncode(base64(pic))
            @Field("version") version: String
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

    companion object {
        const val HEADER_TOKEN = "x-auth-token"
    }

}
