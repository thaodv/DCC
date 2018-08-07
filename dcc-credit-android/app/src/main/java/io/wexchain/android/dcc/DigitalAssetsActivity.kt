package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.withTransitionEnabled
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.modules.selectnode.SelectNodeActivity
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalAssetsBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency

class DigitalAssetsActivity : BindActivity<ActivityDigitalAssetsBinding>(), ItemViewClickListener<DigitalCurrency> {

    override val contentLayoutId: Int = R.layout.activity_digital_assets


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setupTransitions()


        binding.ibAdd.setOnClickListener {
            startActivity(Intent(this, SearchDigitalCurrencyActivity::class.java))
        }
        binding.btnShare.setOnClickListener {
            showShare()
        }
    }

    override fun onResume() {
        super.onResume()

        val assetsVm = getViewModel<DigitalAssetsVm>()
        assetsVm.ensureHolderAddress(this)
        assetsVm.assets.observe(this, Observer {
            adapter.setList(it)
        })
        binding.assets = assetsVm
        adapter.assetsVm = assetsVm
        binding.rvAssets.adapter = adapter

        binding.assets!!.updateHoldingAndQuote()
    }

    private fun showShare() {
//        ShareHelper.showShareWithCapture(this, window.decorView)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val adapter = DigitalAssetsAdapter(this)

}
