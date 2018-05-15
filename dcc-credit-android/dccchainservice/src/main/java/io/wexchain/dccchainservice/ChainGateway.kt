package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import retrofit2.http.*


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

    @GET("ca/1/getContractAddress")
    fun getCaContractAddress(): Single<Result<String>>

    /* ca */
    @POST("ca/1/uploadPubKey")
    @FormUrlEncoded
    fun uploadCaPubKey(
            @Field("ticket") ticket: String,
            @Field("signMessage") signMessage: String,
            @Field("code") code: String? = null
    ): Single<Result<String>>

    @POST("ca/1/deletePubKey")
    @FormUrlEncoded
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

    @GET("dcc/cert/1/getContractAddress")
    fun getCertContractAddress(@Query("business") business: String): Single<Result<String>>


    @POST("dcc/cert/1/apply")
    @FormUrlEncoded
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

    @POST("dcc/loan/1/apply")
    @FormUrlEncoded
    fun applyLoan(
        @Field("ticket") ticket: String,
        @Field("signMessage") signMessage: String,
        @Field("code") code: String? = null
    ):Single<Result<String>>

    @Deprecated("replaced")
    @POST("dcc/loan/1/cancel")
    @FormUrlEncoded
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
    }
}