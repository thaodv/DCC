package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/27 21:34.
 * usage:
 */
data class CashierContentBean(
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("name") val name: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("id") val id: String
) {
}
