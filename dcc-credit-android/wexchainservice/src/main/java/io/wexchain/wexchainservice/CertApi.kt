package io.wexchain.wexchainservice

import io.reactivex.Single
import io.wexchain.wexchainservice.domain.CertOrder
import io.wexchain.wexchainservice.domain.Result
import io.wexchain.wexchainservice.domain.TicketResponse
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by sisel on 2018/2/8.
 */
interface CertApi {

    @GET("getContractAddress")
    fun getContractAddressId(@Query("business") business: String = BUSINESS_ID): Single<Result<String>>

    @POST("apply")
    @FormUrlEncoded
    fun applyId(
        @Field("ticket") ticket: String,
        @Field("signMessage") signMessage: String,
        @Field("code") code: String? = null,
        @Field("business") business: String = BUSINESS_ID
    ): Single<Result<String>>

    @GET("getOrderByTx")
    fun getOrderByTx(
        @Query("txHash") txHash: String,
        @Query("business") business: String = BUSINESS_ID
    ): Single<Result<CertOrder>>

    @GET("getData")
    fun getCertDataByAddress(
        @Query("address") address: String,
        @Query("business") business: String = BUSINESS_ID
    ): Single<Result<CertOrder>>


    companion object {
        //ID：实名认证
        //BANK_LOG：银行流水
        //CREDIT_CARD：信用卡
        //CREDIT：信用
        //COMMUNICATION_LOG：通信记录信息
        const val BUSINESS_ID = "ID"
        const val BUSINESS_BANK_LOG = "BANK_LOG"
        const val BUSINESS_CREDIT_CARD = "CREDIT_CARD"
        const val BUSINESS_CREDIT = "CREDIT"
        const val BUSINESS_COMMUNICATION_LOG = "COMMUNICATION_LOG"

        fun certUrl(baseUrl: String): String {
            return "${baseUrl}cert/1/"
        }
    }
}