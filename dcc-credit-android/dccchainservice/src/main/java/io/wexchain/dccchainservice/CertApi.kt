package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.IdOcrInfo
import io.wexchain.dccchainservice.domain.Result
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by sisel on 2018/2/8.
 */
interface CertApi {

    @POST("dcc/cert/id/verify")
    @Multipart
    fun idVerify(
            @Part("address") address:String,
            @Part("orderId") orderId: Long,
            @Part("realName") realName: String,
            @Part("certNo") certNo: String,
            @Part file: MultipartBody.Part,
            @Part("signature") signature: String
    ):Single<Result<CertOrder>>

    @POST("dcc/cert/id/ocr")
    @Multipart
    fun idOcr(@Part file: MultipartBody.Part): Single<Result<IdOcrInfo>>

    @POST("dcc/cert/bankCard/verify")
    @FormUrlEncoded
    fun bankCardVerify(
            @Field("address") address:String,
            @Field("orderId") orderId:Long,
            @Field("bankCode") bankCode:String,
            @Field("bankAccountNo") bankAccountNo:String,
            @Field("accountName") accountName:String,
            @Field("certNo") certNo:String,
            @Field("phoneNo") phoneNo:String,
            @Field("signature") signature:String
    ):Single<Result<String>>

    @POST("dcc/cert/bankCard/verify/advance")
    @FormUrlEncoded
    fun bankCardAdvance(
            @Field("address") address:String,
            @Field("orderId") orderId:Long,
            @Field("ticket") ticket:String,
            @Field("validCode") validCode:String,
            @Field("signature") signature:String
    )

    @POST("dcc/cert/communicationLog/get/data")
    @FormUrlEncoded
    fun requestCommunicationLogData(
            @Field("address") address:String,
            @Field("orderId") orderId:Long,
            @Field("userName") userName: String,
            @Field("certNo") certNo: String,
            @Field("phoneNo") phoneNo: String,
            @Field("pssword") password: String,
            @Field("signature") signature:String
    ):Single<Result<String>>//todo response type

    @POST("dcc/cert/communicationLog/get/data/advance")
    @FormUrlEncoded
    fun requestCommunicationLogDataAdvance(
            @Field("address") address:String,
            @Field("orderId") orderId:Long,
            @Field("userName") userName: String,
            @Field("certNo") certNo: String,
            @Field("phoneNo") phoneNo: String,
            @Field("pssword") password: String,
            @Field("token") token: String,
            @Field("processCode") processCode: String,
            @Field("captcha") captcha: String,
            @Field("website") website: String,
            @Field("queryPwd") queryPwd: String?,
            @Field("signature") signature:String
    ):Single<Result<String>>//todo response type

    @POST("dcc/cert/communicationLog/get/report")
    @FormUrlEncoded
    fun getCommunicationLogReport(
            @Field("address") address:String,
            @Field("orderId") orderId:Long,
            @Field("userName") userName: String,
            @Field("certNo") certNo: String,
            @Field("phoneNo") phoneNo: String,
            @Field("pssword") password: String,
            @Field("signature") signature:String
    ):Single<Result<String>>

    companion object {

        fun uploadFilePart(data: ByteArray, name: String, mimeType: String): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                    "file",
                    name,
                    RequestBody.create(
                            MediaType.parse(mimeType),
                            data
                    )
            )
        }
    }
}