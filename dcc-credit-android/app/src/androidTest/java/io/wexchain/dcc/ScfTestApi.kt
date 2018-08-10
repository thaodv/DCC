package io.wexchain.dcc

import io.reactivex.Single
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.Result
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ScfTestApi{
    @GET("secure/ping")
    fun testPing(@Header(ScfApi.HEADER_TOKEN) token:String?): Single<Response<Result<String>?>>
}
