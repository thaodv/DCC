package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class QueryStoreBean(
        @SerializedName("id") val id: String,
        @SerializedName("status") val status: Status,
        @SerializedName("amount") val amount: String,
        @SerializedName("memberId") val memberId: String,
        @SerializedName("openId") val openId: String,
        @SerializedName("openId") val appId: String
) {
    enum class Status {
        ACCEPTED,//"转移中"

        DELIVERED; //"已完成"
    }
}
