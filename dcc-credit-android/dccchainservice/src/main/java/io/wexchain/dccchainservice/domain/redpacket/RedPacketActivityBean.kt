package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class RedPacketActivityBean(
        @SerializedName("id") val id: String,
        @SerializedName("description") val description: String,
        @SerializedName("bannerImgUrl") val bannerImgUrl: String,
        @SerializedName("coverImgUrl") val coverImgUrl: String,
        @SerializedName("bannerLinkUrl") val bannerLinkUrl: String,
        @SerializedName("status") val status: Status,
        @SerializedName("from") val from: Long,
        @SerializedName("to") val to: Long
) {
    enum class Status {
        /**
         * 已创建
         */
        CREATED,
        /**
         * 已上架
         */
        SHELVED,
        /**
         * 已开始
         */
        STARTED,
        /**
         * 已结束
         */
        ENDED
    }
}
