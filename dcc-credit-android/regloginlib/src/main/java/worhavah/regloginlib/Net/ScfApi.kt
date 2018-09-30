package worhavah.regloginlib.Net

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.ScfAccountInfo
import retrofit2.Response
import retrofit2.http.*

interface ScfApi {
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("nonce") nonce: String,//否	活动code
        @Field("address") address: String,//否	钱包地址
        @Field("signature") sign: String,//否	签名
        @Field("username") username: String,//否	用户名，值为钱包地址
        @Field("password") password: String? = null    //是	传空
    ): Single<Response<Result<String>>>

    @GET("auth/getNonce")
    fun getNonce(): Single<Result<String>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("member/register")
    @FormUrlEncoded
    fun scfRegister(
        @Field("nonce") nonce: String,//否	活动code
        @Field("address") address: String,//否	钱包地址
        @Field("signature") sign: String,//否	签名
        @Field("loginName") loginName: String,//否	用户名，值为钱包地址
        @Field("inviteCode") inviteCode:String?
    ):Single<Response<Result<String>>>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("member/getByIdentity")
    @FormUrlEncoded
    fun getScfMemberInfo(
        @Field("nonce") nonce: String,//否	活动code
        @Field("address") address: String,//否	钱包地址
        @Field("signature") sign: String//否	签名
    ):Single<Result<ScfAccountInfo>>

    companion object {
        const val HEADER_TOKEN = "x-auth-token"
    }

}
