package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/13 14:36.
 * usage:
 */
data class TrustAssetBean(
        @SerializedName("id") val id: String,
        @SerializedName("url") val url: String,
        @SerializedName("rank") val rank: String,
        @SerializedName("cryptoAssetConfig") val cryptoAssetConfig: CryptoAssetConfigBean
) {
    data class CryptoAssetConfigBean(
            @SerializedName("accountType") val accountType: String,
            @SerializedName("id") val id: String,
            @SerializedName("code") val code: String,
            @SerializedName("name") val name: String,
            @SerializedName("productCode") val productCode: String,
            @SerializedName("extParams") val extParams: ExtParamsBean,
            @SerializedName("depositEnabled") val depositEnabled: Boolean
    ) {
        data class ExtParamsBean(
                @SerializedName("CONTRACT_ADDRESS") val CONTRACT_ADDRESS: String,
                @SerializedName("COLLECT_THRESHOLD") val COLLECT_THRESHOLD: String
        )
    }
}
