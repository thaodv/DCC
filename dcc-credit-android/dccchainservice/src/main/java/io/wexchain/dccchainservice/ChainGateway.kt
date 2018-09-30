package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import retrofit2.http.*

/**
 * Api doc see below
 * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=58757265]
 */
interface ChainGateway {

    /* generic */
    @POST("ticket/1/getTicket")
    fun getTicket(): Single<Result<TicketResponse>>

    @GET("receipt/1/hasReceipt")
    fun hasReceipt(@Query("txHash") txHash: String): Single<Result<Boolean>>

    @GET("receipt/1/getReceiptResult")
    fun getReceiptResult(@Query("txHash") txHash: String):Single<Result<ReceiptResult>>

    @GET("erc20/{symbol}/1/getContractAddress")
    fun getErc20ContractAddress(@Path("symbol") symbol: String): Single<Result<String>>

    @GET("contract/1/getContractAddress/bsx_01")
    fun getBiintContractAddress(): Single<Result<String>>

    @GET("ca/1/getContractAddress")
    fun getCaContractAddress(): Single<Result<String>>

    /* ca */
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("ca/1/uploadPubKey")
    fun uploadCaPubKey(
            @Field("ticket") ticket: String,
            @Field("signMessage") signMessage: String,
            @Field("code") code: String? = null
    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("ca/1/deletePubKey")
    fun deleteCaPubKey(
            @Field("ticket") ticket: String,
            @Field("signMessage") signMessage: String,
            @Field("code") code: String?
    ): Single<Result<String>>

    /**
     * returns octet stream of key (cert)
     */
    @GET("ca/1/getPubKey")
    fun getPubKey(@Query("address") address: String): Single<Result<String>>

    /* cert */
    @GET("dcc/cert/1/getAbi")
    fun getContractAbi(@Query("business") business: String): Single<Result<String>>

    /**
     * 获取合约地址
     */
    @GET("dcc/cert/1/getContractAddress")
    fun getCertContractAddress(@Query("business") business: String): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("dcc/cert/1/apply")
    fun certApply(
            @Field("ticket") ticket: String,
            @Field("signMessage") signMessage: String,
            @Field("code") code: String? = null,
            @Field("business") business: String
    ): Single<Result<String>>

    @GET("dcc/cert/1/getData")
    fun getCertData(
            @Query("address") address: String,
            @Query("business") business: String
    ): Single<Result<CertData>>

    @GET("dcc/cert/1/getOrderByTx")
    fun getOrderByTx(
            @Query("txHash") txHash: String,
            @Query("business") business: String
    ): Single<Result<CertOrder>>

    @GET("dcc/cert/1/getOrdersByTx")
    fun getOrdersByTx(
            @Query("txHash") txHash: String,
            @Query("business") business: String
    ): Single<Result<List<CertOrder>>>

    @GET("dcc/cert/1/getOrderUpdatedEventsByTx")
    fun getOrderUpdatedEventsByTx(
            @Query("txHash") txHash: String,
            @Query("business") business: String
    ): Single<Result<List<CertOrderUpdatedEvent>>>

    @GET("dcc/cert/1/getOrder")
    fun getOrderByOrderId(
            @Query("orderId") orderId: Long,
            @Query("business") business: String
    ): Single<Result<CertOrder>>

    @GET("dcc/cert/1/getExpectedFee")
    fun getExpectedFee(
            @Query("business") business: String
    ):Single<Result<String>>

    /* loan */
    @GET("dcc/loan/1/getContractAddress")
    fun getLoanContractAddress():Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("dcc/loan/1/apply")
    fun applyLoan(
        @Field("ticket") ticket: String,
        @Field("signMessage") signMessage: String,
        @Field("code") code: String? = null
    ):Single<Result<String>>

    @Deprecated("replaced")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("dcc/loan/1/cancel")
    fun cancelLoanOrder(
        @Field("signMessage") signMessage: String
    ):Single<Result<String>>

    @GET("dcc/loan/1/getOrderByTx")
    fun getLoanOrdersByTx(
        @Query("txHash") txHash: String
    ): Single<Result<List<LoanChainOrder>>>


    companion object {
        const val BUSINESS_ID = "ID"
        const val BUSINESS_BANK_CARD = "BANK_CARD"
        const val BUSINESS_COMMUNICATION_LOG = "COMMUNICATION_LOG"
        const val TN_COMMUNICATION_LOG = "TN_COMMUNICATION_LOG"
    }
}
