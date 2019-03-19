package io.wexchain.android.dcc.view.adapters

import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.diffCallbackById
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemDigitalCurrencySummaryBinding
import io.wexchain.digitalwallet.DigitalCurrency

class DigitalAssetsAdapter(itemViewClickListener: ItemViewClickListener<DigitalCurrency>?) :
        DataBindAdapter<ItemDigitalCurrencySummaryBinding, DigitalCurrency>(
                R.layout.item_digital_currency_summary,
                diffCallbackById { it.id() },
                itemViewClickListener
        ) {
    internal lateinit var assetsVm: DigitalAssetsVm

    var isShow: Boolean = true

    init {
        setHasStableIds(true)
    }


    override fun bindData(binding: ItemDigitalCurrencySummaryBinding, item: DigitalCurrency?) {
        binding.holdingMap = assetsVm.holding
        binding.quoteMap = assetsVm.quote
        binding.isShow = isShow
        binding.dc = item
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id() ?: super.getItemId(position)
    }


}
