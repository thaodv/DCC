package worhavah.certs.tools

import io.reactivex.Single
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import worhavah.certs.bean.*
import worhavah.certs.beans.BeanValidHomeResult
import worhavah.certs.beans.BeanValidMailResult
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.beans.Beancitys

/**
 * Created by sisel on 2018/2/8.
 */
interface tnCertApi {

    @POST("dcc/endorsement/tongniu/communicationLog2/create/task")
    @Multipart
    fun requestCommunicationLogData(
        @Part("address") address:String,
        @Part("orderId") orderId:Long,
        @Part("userName") userName: String,
        @Part("certNo") certNo: String,
        @Part("phoneNo") phoneNo: String,
        @Part("password") password: String,
        @Part("nonce") nonce: String,
        @Part("signature") signature:String
    ):Single<Result<TNcert1new>>


    @POST("dcc/endorsement/tongniu/communicationLog2/advance")
    @Multipart
    fun TNAdvance(
        @Part("address") address:String,
        @Part("orderId") orderId:Long,
        @Part("taskStage") taskStage: String,
        @Part("authCode") authCode: String?,
        @Part("smsCode") smsCode: String? ,
        @Part("signature") signature:String
        ):Single<Result<TNcert1new>>

    @POST("dcc/endorsement/tongniu/communicationLog2/resend/code")
    @Multipart
    fun TNGetcode(
        @Part("address") address:String,
        @Part("orderId") orderId:Long,

        @Part("signature") signature:String
    ):Single<Result<TNcert1new>>

    @POST("dcc/endorsement/tongniu/communicationLog2/getReport")
    @Multipart
    fun TNgetReport(
        @Part("address") address:String,
        @Part("orderId") orderId:Long,

        @Part("signature") signature:String
    ):Single<Result<TNcert1newreport>>

    @POST("dcc/endorsement/tongniu/communicationLog2/getReport")
    @Multipart
    fun TNgetReport2(
        @Part("address") address:String,
        @Part("orderId") orderId:Long,

        @Part("signature") signature:String
    ):Single< String >



}