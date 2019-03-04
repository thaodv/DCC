package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/4 14:38.
 * usage:
 */
data class CheckCodeBean(
        @SerializedName("pubKey") val pubKey: String,
        @SerializedName("salt") val salt: String
)
