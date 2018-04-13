package io.wexchain.dccchainservice.domain

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


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

    companion object {
        //eg 2018-04-13T17:15:23.699+08:00
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.CHINA)
    }
}