package io.wexchain.dccchainservice

import io.reactivex.Single
import io.wexchain.dccchainservice.domain.Result
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ScfApi {
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
    fun getNonce():Single<Result<String>>

    companion object {

        const val HEADER_TOKEN = "x-auth-token"
    }
}