package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/28 15:57.
 * usage:
 */
data class QueryGoodsOrderPageBean(
        @SerializedName("id") val id: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("lastUpdatedTime") val lastUpdatedTime: Long,
        @SerializedName("payerMemo") val payerMemo: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("receivedAmount") val receivedAmount: String,
        @SerializedName("goods") val goods: GoodsBean,
        @SerializedName("status") val status: Status
) {
    data class GoodsBean(
            @SerializedName("name") val name: String,
            @SerializedName("description") val description: String,
            @SerializedName("assetCode") val assetCode: String
    )

    enum class Status {
        CREATED,//("有效"),

        SUCCESS,//("成功"),

        FAILURE//("失败");
    }

}
