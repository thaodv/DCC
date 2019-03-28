package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/27 21:34.
 * usage:
 */
data class CashierContentBean(
        @SerializedName("order") val order: OrderBean
) {
    data class OrderBean(
            @SerializedName("id") val id: String,
            @SerializedName("name") val name: String,
            @SerializedName("amount") val amount: String,
            @SerializedName("assetCode") val assetCode: String)
}
