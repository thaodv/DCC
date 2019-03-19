package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.diffCallbackById
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemDigitalCurrencyQscanSelectBinding
import io.wexchain.digitalwallet.DigitalCurrency

class DigitalAssetsChooseAdapter(itemViewClickListener: ItemViewClickListener<DigitalCurrency>?) :
        DataBindAdapter<ItemDigitalCurrencyQscanSelectBinding, DigitalCurrency>(
                R.layout.item_digital_currency_qscan_select,
                diffCallbackById { it.id() },
                itemViewClickListener
        ) {
    init {
        setHasStableIds(true)
    }

    override fun bindData(binding: ItemDigitalCurrencyQscanSelectBinding, item: DigitalCurrency?) {
        binding.dc = item
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id() ?: super.getItemId(position)
    }

}
