package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.util.DateUtil

/**
 * Created by sisel on 2018/3/17.
 */

data class AccrossTransRecordList(
        val totalElements: Int,
        val totalPages: Int,
        val sortPageParam: SortPageParam,
        val items: List<Item>
) {
    data class Item(
            val amount: String,
            val status: Status,
            val exchangeTime: Long,
            val serviceCharge: String,
            val fromAssetCode: String,
            val toChain: String
    ) {

        enum class Status(
                val description: String
        ) {
            ACCEPTED("转移中"),

            DELIVERED("已完成");
        }

        fun isPublic2Private(): Boolean {
            return fromAssetCode == "DCC" && toChain == "DCC_JUZIX"
        }

        fun isPrivate2Public(): Boolean {
            return fromAssetCode == "DCC_JUZIX" && toChain == "DCC"
        }

        fun getExChangeTime(): String {
            return DateUtil.getStringTime(exchangeTime, "yyyy-MM-dd HH:mm:ss")
        }

    }

    data class SortPageParam(
            val number: Int,
            val size: Int,
            val sortParamList: List<SortParam>
    )

    data class SortParam(
            val direction: String,
            val property: String
    )
}
