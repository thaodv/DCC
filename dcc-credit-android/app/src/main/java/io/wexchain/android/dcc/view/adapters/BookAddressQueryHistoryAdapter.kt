package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.QueryHistory
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemQueryHistoryAddressBinding

class BookAddressQueryHistoryAdapter(
) : DataBindAdapter<ItemQueryHistoryAddressBinding, QueryHistory>(
        layout = R.layout.item_query_history_address,
        itemDiffCallback = itemDiffCallback({ a, b -> a.name == b.name })
) {
    override fun bindData(binding: ItemQueryHistoryAddressBinding, item: QueryHistory?) {
        binding.addr = item
    }

}
