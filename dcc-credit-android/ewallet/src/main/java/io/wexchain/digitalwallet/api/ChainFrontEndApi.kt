package io.wexchain.digitalwallet.api

import io.reactivex.Single
import io.wexchain.digitalwallet.api.domain.front.CResult
import io.wexchain.digitalwallet.api.domain.front.Paged
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.api.domain.front.TokenMetaInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by sisel on 2018/1/23.
 * token related frontend api
 */
interface ChainFrontEndApi {

    @POST("token/query")
    @FormUrlEncoded
    fun searchToken(
            @Field("search") search: String,
            @Field("pageNo") pageNo: Int = 1,
            @Field("pageSize") pageSize: Int = Int.MAX_VALUE
    ): Single<CResult<Paged<TokenMetaInfo>>>

    @POST("quote/query/today/variety")
    @FormUrlEncoded
    fun getQuotes(@Field("varietyCodes") varietyCodes: String): Single<CResult<List<Quote>>>
}