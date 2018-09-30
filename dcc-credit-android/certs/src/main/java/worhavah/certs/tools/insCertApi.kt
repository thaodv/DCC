package worhavah.certs.tools

import io.reactivex.Single
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import worhavah.certs.beans.BeanValidHomeResult
import worhavah.certs.beans.BeanValidMailResult
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.beans.Beancitys

/**
 * Created by sisel on 2018/2/8.
 */
interface insCertApi {



    //实名认证接口（需要登录）
    @POST("secure/certification/id/apply")
    @Multipart
    fun apply(
            @Header(ScfApi.HEADER_TOKEN) token: String?,
          //  @Part("address") address:String,
            @Part("orderId") orderId: Long,
            @Part("realName") realName: String,
            @Part("certNo") certNo: String,
            @Part file: MultipartBody.Part
          //  @Part("signature") signature: String
    ):Single<Result<CertOrder>>


    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("secure/certification/mobile/verification")
    fun mobileverif(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,
        @Field("phoneNum") pn: String
    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("secure/certification/mobile/verification/advance")
    fun mobileverifadvance(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,
        @Field("phoneNum") pn: String,
        @Field("verifyCode") verifyCode: String
    ): Single<Result<BeanValidResult>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("secure/certification/mail/verification")
    fun mailverif(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,
        @Field("mailAddress") mailAddress: String
    ): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("secure/certification/mail/verification/advance")
    fun mailverifadvance(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,
        @Field("mailAddress") mailAddress: String,
        @Field("verifyCode") verifyCode: String
    ): Single<Result<BeanValidMailResult>>


    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("secure/certification/homeAddress/advance")
    fun homeAddressveri(
        @Header(ScfApi.HEADER_TOKEN) token: String?,
        @Field("orderId") orderId: Long,
        @Field("homeAddress") homeAddress: String,
        @Field("homeLocationCode") homeLocationCode: String
    ): Single<Result<BeanValidHomeResult>>

    @POST("location/getJsonString")
    fun getJsonString(
    ):Single<Beancitys>


    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("dcc/cert/1/apply")
    fun certApply(
        @Field("ticket") ticket: String,
        @Field("signMessage") signMessage: String,
        @Field("code") code: String? = null,
        @Field("business") business: String
    ): Single<Result<String>>

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