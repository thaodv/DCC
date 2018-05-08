package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName

data class PagedList<T>(
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("sortPageParam")
    val sortPageParam: SortPageParam,
    @SerializedName("items")
    val items: List<T>
) {

    data class SortPageParam(
        @SerializedName("number")
        val number: Int,
        @SerializedName("size")
        val size: Int,
        @SerializedName("sortParamList")
        val sortParamList: List<String>
    )
}