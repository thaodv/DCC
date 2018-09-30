package worhavah.regloginlib.Net.beans

/**
 * Created by sisel on 2018/3/17.
 */

data class PrivateTokenTransferList(
        val totalElements: Int,
        val totalPages: Int,
        val sortPageParam: SortPageParam,
        val items: List<Item>
){
    data class Item(
            val contractAddress: String,
            val logIndex: Int,
            val blockHash: String,
            val blockNumber: Long,
            val blockTimestamp: Long,
            val transactionHash: String,
            val transactionIndex: Int,
            val fromAddress: String,
            val toAddress: String,
            val value: String
    )

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
