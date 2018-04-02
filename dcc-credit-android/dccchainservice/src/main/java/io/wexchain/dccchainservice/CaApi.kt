package io.wexchain.dccchainservice

import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.TicketResponse
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by lulingzhi on 2017/11/15.
 * passport auth ca gateway
 */
interface CaApi {

    @POST("getTicket")
    fun getTicket():Single<Result<TicketResponse>>

    @POST("uploadPubKey")
    @FormUrlEncoded
    fun uploadPubKey(@Field("ticket") ticket:String,
                     @Field("signMessage") signMessage:String,
                     @Field("code") code:String?):Single<Result<String>>

    @POST("deletePubKey")
    @FormUrlEncoded
    fun deletePubKey(@Field("ticket") ticket:String,
                     @Field("signMessage") signMessage:String,
                     @Field("code") code:String?):Single<Result<String>>

    @POST("hasReceipt")
    @FormUrlEncoded
    fun hasReceipt(@Field("txHash") txHash:String):Single<Result<Boolean>>

    /**
     * returns octet stream of key (cert)
     */
    @GET("getPubKey")
    fun getPubKey(@Query("address") address:String):Single<Result<String>>


    /**
     * call back
     */
    @POST()
    @FormUrlEncoded
    fun callback(
            @Url url:String,
            @Field("address") address: String,
            @Field("signature") signature: String,
            @Field("nonce") nonce: String
    ): Single<Result<String?>>

    @GET("getContractAddress")
    fun getContractAddress(): Single<Result<String>>

    companion object {
        fun caUrl(baseUrl: String): String {
            return baseUrl + "ca/1/"
        }
    }
}