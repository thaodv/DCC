package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/28 15:57.
 * usage:
 */
data class QueryGoodsOrderPageBean(
        @SerializedName("id") val id: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("payerMemo") val payerMemo: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("goods") val goods: GoodsBean
) {
    data class GoodsBean(
            @SerializedName("name") val name: String,
            @SerializedName("assetCode") val assetCode: String
    )

}
