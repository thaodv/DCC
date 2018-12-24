package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class RedPacketActivityBean(
        @SerializedName("id") val id: String,
        @SerializedName("merchantCode") val merchantCode: String,
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("totalAmount") val totalAmount: String,
        @SerializedName("description") val description: String,
        @SerializedName("bannerImgUrl") val bannerImgUrl: String,
        @SerializedName("coverImgUrl") val coverImgUrl: String,
        @SerializedName("bannerLinkUrl") val bannerLinkUrl: String,
        @SerializedName("status") val status: Status,
        @SerializedName("lastUpdatedTime") val lastUpdatedTime: String,
        @SerializedName("startTime") val startTime: String,
        @SerializedName("endTime") val endTime: String
) {
    enum class Status {
        STARTED
    }
}
