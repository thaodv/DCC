package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.QueryHistory
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemQueryHistoryAddressBinding

class BookAddressQueryHistoryAdapter(
        itemViewClickListener: ItemViewClickListener<QueryHistory>
) : DataBindAdapter<ItemQueryHistoryAddressBinding, QueryHistory>(
        layout = R.layout.item_query_history_address,
        itemDiffCallback = itemDiffCallback({ a, b -> a.name == b.name }),
        itemViewClickListener = itemViewClickListener
) {
    override fun bindData(binding: ItemQueryHistoryAddressBinding, item: QueryHistory?) {
        binding.addr = item
    }

}
