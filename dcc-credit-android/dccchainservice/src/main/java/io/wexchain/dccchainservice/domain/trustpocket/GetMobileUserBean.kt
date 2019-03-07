package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/7 15:29.
 * usage:
 */
data class GetMobileUserBean(
        @SerializedName("id") val id: String,
        @SerializedName("mobile") val mobile: String
)
