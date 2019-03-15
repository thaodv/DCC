package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/14 20:43.
 * usage:
 */
data class GetBalanceBean(
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("availableAmount") val availableAmount: AvailableAmountBean
) {
    data class AvailableAmountBean(
            @SerializedName("assetValue") val assetValue: AssetValueBean,
            @SerializedName("legalTenderPrice") val legalTenderPrice: LegalTenderPriceBean
    ) {
        data class AssetValueBean(
                @SerializedName("assetCode") val assetCode: String,
                @SerializedName("amount") val amount: String
        )

        data class LegalTenderPriceBean(
                @SerializedName("assetCode") val assetCode: String,
                @SerializedName("amount") val amount: String
        )
    }
}
