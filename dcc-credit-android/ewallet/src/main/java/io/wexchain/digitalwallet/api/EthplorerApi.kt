package io.wexchain.digitalwallet.api

import cc.sisel.ewallet.BuildConfig
import io.reactivex.Single
import io.wexchain.digitalwallet.api.domain.EpTransactions
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sisel on 2018/1/25.
 */
interface EthplorerApi {
    @GET("getAddressHistory/{address}")
    fun getAddressHistory(
            @Path("address") address: String,
            @Query("limit") limit: Int = 10,
            @Query("token") token: String,
            @Query("type") type: String = "transfer",
            @Query("apiKey") apiKey: String = BuildConfig.ETHPLORER_API_KEY
    ): Single<EpTransactions>
}