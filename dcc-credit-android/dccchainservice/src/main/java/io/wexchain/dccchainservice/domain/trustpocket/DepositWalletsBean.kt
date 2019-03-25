package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/4 15:46.
 * usage:
 */
data class DepositWalletsBean(
        @SerializedName("address") val address: String,
        @SerializedName("chainCode") val chainCode: String,
        @SerializedName("assetCode") val assetCode: String
)
