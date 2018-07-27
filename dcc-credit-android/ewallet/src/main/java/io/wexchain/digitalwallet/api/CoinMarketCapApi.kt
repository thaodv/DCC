package io.wexchain.digitalwallet.api

import io.reactivex.Single
import io.wexchain.digitalwallet.api.domain.front.CResult
import io.wexchain.digitalwallet.api.domain.front.CoinDetail
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Created by Wangpeng on 2018/7/25 13:25.
 * usage:
 */
interface CoinMarketCapApi {

    /**
     * @see [http://wiki.weihui.com:9080/pages/viewpage.action?pageId=60654449]
     */
    @GET("redis/coinMarketCap")
    fun coinMarketCap(@Query("coinTypes") coinTypes: String): Single<CResult<List<CoinDetail>>>

}
