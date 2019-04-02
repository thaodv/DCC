package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/4/2 13:58.
 * usage:
 */
class GetBalanceLogBean(
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("summary") val summary: String,
        @SerializedName("type") val type: TypeBean
) {
    enum class TypeBean {
        IN, OUT
    }
}
