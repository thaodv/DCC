package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemAddressBookBinding

class AddressBookAdapter(
        itemViewClickListener: ItemViewClickListener<AddressBook>
) : DataBindAdapter<ItemAddressBookBinding, AddressBook>(
        layout = R.layout.item_address_book,
        itemDiffCallback = itemDiffCallback({ a, b -> a.address == b.address }),
        itemViewClickListener = itemViewClickListener
) {

    override fun bindData(binding: ItemAddressBookBinding, item: AddressBook?) {
        binding.addr = item
    }
}
