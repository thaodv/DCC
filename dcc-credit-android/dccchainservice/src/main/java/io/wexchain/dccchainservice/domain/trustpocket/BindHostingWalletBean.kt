package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/6 14:50.
 * usage:
 */
data class BindHostingWalletBean(
        @SerializedName("mobileUserId") val mobileUserId: String
)
