package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import retrofit2.Response
import retrofit2.http.*

interface ScfApi {
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

    @POST("loan/product/queryByLenderCode")
    @FormUrlEncoded
    fun queryLoanProductsByLenderCode(
        @Field("lenderCode") lenderCode: String? = null
    ): Single<Result<List<LoanProduct>>>

    @POST("loan/product/getById")
    @FormUrlEncoded
    fun queryLoanProductById(
        @Field("id") id: Long
    ): Single<Result<LoanProduct>>

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

    @POST("secure/loan/getLastOrder")
    fun getLastOrder(
        @Header(ScfApi.HEADER_TOKEN) token: String?
    ): Single<Result<LoanChainOrder>>

    @POST("secure/loan/credit/apply")
    @FormUrlEncoded
    fun applyLoanCredit(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,//否	链上订单id
        @Field("loanProductId") loanProductId: Long,//否	借款产品id
        @Field("borrowName") borrowName: String,//否	借款人真实姓名
        @Field("borrowAmount") borrowAmount: String, //    否	借款金额（11，4）
        @Field("borrowDuration") borrowDuration: Int,//否	借款期限
        @Field("durationType") durationType: String,//否	借款期限单位（YEAR，MONTH，DAY）
        @Field("certNo") certNo: String,//否	身份证件号
        @Field("mobile") mobile: String,//否	借款人手机号（手机认证）
        @Field("bankCard") bankCard: String,//否	银行卡号
        @Field("bankMobile") bankMobile: String,//否	银行卡预留绑定手机号
        @Field("applyDate") applyDate: Long//否	申请时间（时间戳）
    ):Single<Result<Long>>

    companion object {
        const val HEADER_TOKEN = "x-auth-token"
    }

}