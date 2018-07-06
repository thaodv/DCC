package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by sisel on 2018/2/8.
 */
interface CertApi {

    @POST("dcc/endorsement/id/verify")
    @Multipart
    fun idVerify(
            @Part("address") address:String,
            @Part("orderId") orderId: Long,
            @Part("realName") realName: String,
            @Part("certNo") certNo: String,
            @Part personalPhoto: MultipartBody.Part,
            @Part frontPhoto: MultipartBody.Part,
            @Part backPhoto: MultipartBody.Part,
            @Part("signature") signature: String,
            @Part("version") version:String
    ):Single<Result<CertOrder>>

    @POST("dcc/endorsement/id/ocr")
    @Multipart
    fun idOcr(@Part file: MultipartBody.Part): Single<Result<IdOcrInfo>>

    @POST("dcc/endorsement/bankCard/verify")
    @Multipart
    fun bankCardVerify(
            @Part("address") address:String,
            @Part("orderId") orderId:Long,
            @Part("bankCode") bankCode:String,
            @Part("bankAccountNo") bankAccountNo:String,
            @Part("accountName") accountName:String,
            @Part("certNo") certNo:String,
            @Part("phoneNo") phoneNo:String,
            @Part("signature") signature:String
    ):Single<Result<String>>

    @POST("dcc/endorsement/bankCard/advance")
    @Multipart
    fun bankCardAdvance(
            @Part("address") address:String,
            @Part("orderId") orderId:Long,
            @Part("ticket") ticket:String,
            @Part("validCode") validCode:String,
            @Part("signature") signature:String
    ):Single<Result<CertOrder>>

    @POST("dcc/endorsement/communicationLog/get/data")
    @Multipart
    fun requestCommunicationLogData(
            @Part("address") address:String,
            @Part("orderId") orderId:Long,
            @Part("userName") userName: String,
            @Part("certNo") certNo: String,
            @Part("phoneNo") phoneNo: String,
            @Part("password") password: String,
            @Part("signature") signature:String
    ):Single<Result<CertProcess>>

    @POST("dcc/endorsement/communicationLog/get/data/advance")
    @Multipart
    fun requestCommunicationLogDataAdvance(
            @Part("address") address:String,
            @Part("orderId") orderId:Long,
            @Part("userName") userName: String,
            @Part("certNo") certNo: String,
            @Part("phoneNo") phoneNo: String,
            @Part("password") password: String,
            @Part("token") token: String,
            @Part("processCode") processCode: String,
            @Part("captcha") captcha: String?,
            @Part("website") website: String,
            @Part("queryPwd") queryPwd: String?,
            @Part("signature") signature:String
    ):Single<Result<CertProcess>>

    @POST("dcc/endorsement/communicationLog/get/report")
    @Multipart
    fun getCommunicationLogReport(
            @Part("address") address:String,
            @Part("orderId") orderId:Long,
            @Part("userName") userName: String,
            @Part("certNo") certNo: String,
            @Part("phoneNo") phoneNo: String,
            @Part("signature") signature:String
    ):Single<Result<CmLogReportData>>

    companion object {

        fun uploadFilePart(data: ByteArray, fileName: String, mimeType: String,name:String="file"): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                    name,
                    fileName,
                    RequestBody.create(
                            MediaType.parse(mimeType),
                            data
                    )
            )
        }
    }
}