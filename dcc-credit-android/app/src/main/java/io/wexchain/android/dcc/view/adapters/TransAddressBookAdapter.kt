package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemAddressLatestUsedBinding

class TransAddressBookAdapter(
        itemViewClickListener: ItemViewClickListener<BeneficiaryAddress>
) : DataBindAdapter<ItemAddressLatestUsedBinding, BeneficiaryAddress>(
        layout = R.layout.item_address_latest_used,
        itemDiffCallback = itemDiffCallback({ a, b -> a.address == b.address }),
        itemViewClickListener = itemViewClickListener,
        clickAwareViewIds = * kotlin.intArrayOf(R.id.tv_add_to_address)
) {

    override fun bindData(binding: ItemAddressLatestUsedBinding, item: BeneficiaryAddress?) {
        binding.addr = item
    }
}
