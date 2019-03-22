package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 13:32.
 * usage:
 */
data class GetAssetOverviewBean(
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("totalPrice") val totalPrice: TotalPriceBean,
        @SerializedName("assetList") val assetList: List<AssetListBean>?
) {
    data class TotalPriceBean(
            @SerializedName("assetCode") val assetCode: String,
            @SerializedName("amount") val amount: String
    )

    data class AssetListBean(
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
