package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/4/18 20:43.
 * usage:
 */
data class ParseBean(
        @SerializedName("beMember") val beMember: Boolean,
        @SerializedName("mobileUser") val mobileUser: Boolean,
        @SerializedName("chainCode") val chainCode: String
        ) {
}
