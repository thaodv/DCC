package worhavah.regloginlib.Net

import io.reactivex.Single
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.Result
import worhavah.regloginlib.Net.beans.CertData
import retrofit2.http.*
import worhavah.regloginlib.Net.beans.CertOrderUpdatedEvent
import worhavah.regloginlib.Net.beans.ReceiptResult
import worhavah.regloginlib.Net.beans.TicketResponse


interface ChainGateway {

    /* generic */
    @POST("ticket/1/getTicket")
    fun getTicket(): Single<Result<TicketResponse>>

    @GET("receipt/1/hasReceipt")
    fun hasReceipt(@Query("txHash") txHash: String): Single<Result<Boolean>>

    @GET("receipt/1/getReceiptResult")
    fun getReceiptResult(@Query("txHash") txHash: String):Single<Result<ReceiptResult>>

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

    /**
     * returns octet stream of key (cert)
     */
    @GET("ca/1/getPubKey")
    fun getPubKey(@Query("address") address: String): Single<Result<String>>

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

    @GET("dcc/cert/1/getExpectedFee")
    fun getExpectedFee(
            @Query("business") business: String
    ):Single<Result<String>>

    companion object {
        const val BUSINESS_ID = "ID"
        const val BUSINESS_BANK_CARD = "BANK_CARD"
        const val BUSINESS_COMMUNICATION_LOG = "COMMUNICATION_LOG"
        const val TN_COMMUNICATION_LOG = "TN_COMMUNICATION_LOG2"
    }
}