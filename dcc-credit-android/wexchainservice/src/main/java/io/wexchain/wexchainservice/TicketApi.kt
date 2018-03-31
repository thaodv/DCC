package io.wexchain.wexchainservice

import io.reactivex.Single
import io.wexchain.wexchainservice.domain.Result
import io.wexchain.wexchainservice.domain.TicketResponse
import retrofit2.http.POST

/**
 * Created by sisel on 2018/2/28.
 */
interface TicketApi {

    @POST("getTicket")
    fun getTicket(): Single<Result<TicketResponse>>

    companion object {
        fun ticketUrl(baseUrl: String): String {
            return baseUrl + "ticket/1/"
        }
    }
}