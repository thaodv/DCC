package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemBookAddressQueryBinding

class BookAddressQueryAdapter(
        itemViewClickListener: ItemViewClickListener<AddressBook>
) : DataBindAdapter<ItemBookAddressQueryBinding, AddressBook>(
        layout = R.layout.item_book_address_query,
        itemDiffCallback = itemDiffCallback({ a, b -> a.address == b.address }),
        itemViewClickListener = itemViewClickListener
) {
    override fun bindData(binding: ItemBookAddressQueryBinding, item: AddressBook?) {
        binding.addr = item
    }

}
