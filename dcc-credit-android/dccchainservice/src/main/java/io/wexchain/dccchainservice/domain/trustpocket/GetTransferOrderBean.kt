package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 21:33.
 * usage:
 */
data class GetTransferOrderBean(
        @SerializedName("id") val id: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("mobile") val mobile: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("amount") val amount: AmountBean,
        @SerializedName("status") val status: Status,
        @SerializedName("memo") val memo: String,
        @SerializedName("splitAmountDetails") val splitAmountDetails: List<SplitAmountDetailsBean>?
) {

    enum class Status {
        SUCCESS, //("成功")
        FAILED, //("失败")
        PROCESSING, //("处理中")
        PAID, //已付款
        CANCELD, //已取消
        CLOSED, //已关闭
        ACCEPTED, //已接受
        REJECTED, //已拒绝
        FAILURE //失败
    }

    data class AmountBean(
            @SerializedName("decimalValue") val decimalValue: String
    )

    data class SplitAmountDetailsBean(

            @SerializedName("memo") val memo: String,
            @SerializedName("amount") val amount: AmountBean) {

        data class AmountBean(
                @SerializedName("decimalValue") val decimalValue: String
        )

    }
}
