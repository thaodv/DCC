package worhavah.regloginlib.Net

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
        val sortParamList: List<SortParam>
    ){

        data class SortParam(
            @SerializedName("direction") val direction: SortDirection,
            @SerializedName("property") val property: String
        )

        enum class SortDirection{
            DESC,
            ASC
        }
    }
}