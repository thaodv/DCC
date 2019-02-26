package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.CertProcess
import io.wexchain.dccchainservice.domain.Result
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by sisel on 2018/2/8.
 */
interface CertApi2 {

    //实名认证接口（需要登录）
    @POST("secure/certification/id/apply")
    @Multipart
    fun apply(
            @Part("orderId") orderId: Long,
            @Part("realName") realName: String,
            @Part("certNo") certNo: String,
            @Part file: MultipartBody.Part
    ): Single<Result<CertOrder>>

    @POST("dcc/endorsement/communicationLog/get/data/advance")
    @Multipart
    fun requestCommunicationLogDataAdvance(
            @Part("address") address: String,
            @Part("orderId") orderId: Long,
            @Part("userName") userName: String,
            @Part("certNo") certNo: String,
            @Part("phoneNo") phoneNo: String,
            @Part("password") password: String,
            @Part("token") token: String,
            @Part("processCode") processCode: String,
            @Part("captcha") captcha: String?,
            @Part("website") website: String,
            @Part("queryPwd") queryPwd: String?,
            @Part("signature") signature: String
    ): Single<Result<CertProcess>>

}
