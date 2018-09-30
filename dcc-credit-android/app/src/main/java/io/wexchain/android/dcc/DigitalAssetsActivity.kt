package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.withTransitionEnabled
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.modules.selectnode.SelectNodeActivity
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalAssetsBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import java.math.BigDecimal

class DigitalAssetsActivity : BindActivity<ActivityDigitalAssetsBinding>(), ItemViewClickListener<DigitalCurrency> {

    override val contentLayoutId: Int = R.layout.activity_digital_assets
    private var tmpList: List<DigitalCurrency>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setupTransitions()
    }

    override fun onResume() {
        super.onResume()

        val assetsVm = getViewModel<DigitalAssetsVm>()
        assetsVm.ensureHolderAddress(this)
        assetsVm.assets.observe(this, Observer {
            tmpList = it
            adapter.setList(it)
        })
        assetsVm.assetsFilter.set(false)
        assetsVm.filterEvent.observe(this, Observer {
            val b = assetsVm.assetsFilter.get()!!
            assetsVm.assetsFilter.set(!b)
            if (b){
                adapter.setList(tmpList)
            }else{
                val tmp = mutableListOf<DigitalCurrency>()
                tmpList!!.forEach {
                    val hoding = assetsVm.holding[it]
                    val balanceStr = ViewModelHelper.getBalanceStr(it, hoding)
                    if (balanceStr != "--" && balanceStr.isNotEmpty()) {
                        val value = BigDecimal(balanceStr)
                        if (value.compareTo(BigDecimal.ZERO) != 0){
                            tmp.add(it)
                        }
                    }
                }
                adapter.setList(tmp)
            }


        })
        binding.assets = assetsVm
        adapter.assetsVm = assetsVm
        binding.rvAssets.adapter = adapter

        binding.assets!!.updateHoldingAndQuote()


    }

    private fun setupTransitions() {
        withTransitionEnabled {
            ViewCompat.setTransitionName(findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
            ViewCompat.setTransitionName(findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
        }
    }

    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        item ?: return
        when (item.chain) {
            Chain.MultiChain -> {
                if (item.symbol == Currencies.DCC.symbol) {
                    navigateTo(DccExchangeActivity::class.java)
                }
            }
            else -> {
                navigateTo(DigitalCurrencyActivity::class.java) {
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, item)
                    putExtra(Extras.EXTRA_DC_SELECTED, true)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_select_node, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.iv_select_node -> {
                navigateTo(SelectNodeActivity::class.java)
                true
            }
            R.id.iv_add_token -> {
                navigateTo(SearchDigitalCurrencyActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val adapter = DigitalAssetsAdapter(this)

}
