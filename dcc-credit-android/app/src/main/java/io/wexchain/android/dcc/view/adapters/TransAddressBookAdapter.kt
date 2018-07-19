package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemAddressLatestUsedBinding

class TransAddressBookAdapter(
        itemViewClickListener: ItemViewClickListener<TransRecord>
) : DataBindAdapter<ItemAddressLatestUsedBinding, TransRecord>(
        layout = R.layout.item_address_latest_used,
        itemDiffCallback = itemDiffCallback({ a, b -> a.address == b.address }),
        itemViewClickListener = itemViewClickListener,
        clickAwareViewIds = * kotlin.intArrayOf(R.id.iv_avatar, R.id.tv_short_name, R.id.tv_address, R.id.tv_add_to_address)
) {

    override fun bindData(binding: ItemAddressLatestUsedBinding, item: TransRecord?) {
        binding.addr = item
    }
}
