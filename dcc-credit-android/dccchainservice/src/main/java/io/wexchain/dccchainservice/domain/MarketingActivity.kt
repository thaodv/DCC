package io.wexchain.dccchainservice.domain

import java.io.Serializable


data class MarketingActivity(
        val merchantCode: String,
        val code: String,
        val name: String,
        val startTime: String,//timestamp
        val endTime: String,//timestamp
        val description: String?,
        val coverImgUrl: String,
        val bannerImgUrl: String?,
        val bannerLinkUrl: String?,
        val status: Status,
        val tags: String
):Serializable {
    enum class Status {
        SHELVED,// 尚未开始
        STARTED,// 已开始
        ENDED,// 已结束
    }
}