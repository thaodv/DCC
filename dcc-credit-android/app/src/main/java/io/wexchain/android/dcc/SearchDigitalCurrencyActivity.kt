package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.diffCallbackById
import io.wexchain.android.dcc.vm.SearchTokenVm
import io.wexchain.android.dcc.vm.SelectedDigitalCurrencies
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivitySearchDigitalCurrencyBinding
import io.wexchain.auth.databinding.ItemDigitalCurrencySelectBinding
import io.wexchain.digitalwallet.DigitalCurrency

/**
 * search , add/remove tokens
 */
class SearchDigitalCurrencyActivity : BindActivity<ActivitySearchDigitalCurrencyBinding>(),
        ItemViewClickListener<DigitalCurrency> {

    override val contentLayoutId: Int = R.layout.activity_search_digital_currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        val vm = ViewModelProviders.of(this).get(SearchTokenVm::class.java)
        val adapter = Adapter(vm.selected, this)
        binding.rvTokens.adapter = adapter
        vm.tokens.observe(this, Observer {
            adapter.setList(it)
        })
        vm.selectedDc.observe(this, Observer {
            adapter.updateSelected(it)
        })
        binding.vm = vm
        binding.btnSubmitNewToken.setOnClickListener {
            startActivity(Intent(this, SubmitNewTokenActivity::class.java))
        }
    }

    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        when (viewId) {
            R.id.ib_select -> {
                binding.vm!!.addSelected(item)
            }
        }
    }

    class Adapter(
            val selected: SelectedDigitalCurrencies,
            itemViewClickListener: ItemViewClickListener<DigitalCurrency>
    ) : DataBindAdapter<ItemDigitalCurrencySelectBinding, DigitalCurrency>(
        R.layout.item_digital_currency_select,
        diffCallbackById { it.id() },
        itemViewClickListener, R.id.ib_select
    ) {

        init {
            setHasStableIds(true)
        }

        override fun bindData(binding: ItemDigitalCurrencySelectBinding, item: DigitalCurrency?) {
            binding.dc = item
            binding.selected = selected
        }

        fun updateSelected(list: List<DigitalCurrency>?) {
            list?.let {
                selected.set.set(it.toSet())
            }
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.id() ?: super.getItemId(position)
        }

    }
}
