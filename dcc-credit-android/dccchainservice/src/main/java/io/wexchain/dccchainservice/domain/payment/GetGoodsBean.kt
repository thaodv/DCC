package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/26 15:24.
 * usage:
 */
data class GetGoodsBean(
        @SerializedName("id") val id: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("amount") val amount: String?,
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("expiredTime") val expiredTime: Long,
        @SerializedName("status") val status: Status,
        @SerializedName("mobile") val mobile: String) {
    enum class Status {
        ACTIVE, CLOSED
    }
}
