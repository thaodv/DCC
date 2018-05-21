package io.wexchain.android.dcc.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface CommonApi{
    @GET
    fun download(@Url url:String):Single<ResponseBody>
}