package io.wexchain.ipfs.net

import io.reactivex.Single
import io.wexchain.ipfs.entity.IpfsVersion
import io.wexchain.ipfs.entity.TokenInfo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 *Created by liuyang on 2018/9/3.
 */
interface IpfsApi {

    @GET()
    fun download(@Url url:String): Single<ResponseBody>

    @POST()
    fun upload(@Url url:String,@Body body: RequestBody): Single<TokenInfo>

    @GET()
    fun getVersion(@Url url: String): Single<IpfsVersion>
}