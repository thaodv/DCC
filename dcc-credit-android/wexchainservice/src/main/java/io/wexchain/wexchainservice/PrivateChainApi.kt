package io.wexchain.wexchainservice

import io.reactivex.Single
import io.wexchain.wexchainservice.domain.PrivateTokenTransferList
import io.wexchain.wexchainservice.domain.Result
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by sisel on 2018/3/17.
 */
interface PrivateChainApi {

    @POST("juzix/tokenTransfer")
    @FormUrlEncoded
    fun tokenTransfer(
            @Field("contractAddress") contractAddress: String,
            @Field("address") address: String,
            // start @ 1 default 1
            @Field("page") page: Int? = null,
            // default 20
            @Field("pageSize") pageSize: Int? = 1000
    ): Single<Result<PrivateTokenTransferList>>
}