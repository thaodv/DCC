package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.View
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.withTransitionEnabled
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.auth.R
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.auth.databinding.ActivityDigitalAssetsBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies

class DigitalAssetsActivity : BindActivity<ActivityDigitalAssetsBinding>(), ItemViewClickListener<DigitalCurrency> {

    override val contentLayoutId: Int = R.layout.activity_digital_assets


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setupTransitions()


        binding.ibClose.setOnClickListener { goFinish() }


        val assetsVm = getViewModel<DigitalAssetsVm>()
        assetsVm.ensureHolderAddress(this)
        assetsVm.assets.observe(this, Observer {
            adapter.setList(it)
        })
        binding.assets = assetsVm
        adapter.assetsVm = assetsVm
        binding.rvAssets.adapter = adapter
        binding.ibAdd.setOnClickListener {
            startActivity(Intent(this, SearchDigitalCurrencyActivity::class.java))
        }
        binding.btnShare.setOnClickListener {
            showShare()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.assets!!.updateHoldingAndQuote()
    }

    private fun showShare() {
//        ShareHelper.showShareWithCapture(this, window.decorView)
    }

    private fun setupTransitions() {
        withTransitionEnabled {
            ViewCompat.setTransitionName(findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
            ViewCompat.setTransitionName(findViewById(R.id.assets_amount_label), Transitions.DIGITAL_ASSETS_AMOUNT_LABEL)
            ViewCompat.setTransitionName(findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
        }
    }

    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        item ?: return
        when(item.chain){
            Chain.MultiChain->{
                if (item.symbol == Currencies.DCC.symbol){
                    navigateTo(DccExchangeActivity::class.java)
                }
            }
            else->{
                navigateTo(DigitalCurrencyActivity::class.java){
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, item)
                    putExtra(Extras.EXTRA_DC_SELECTED, true)
                }
            }
        }
    }


    private val adapter = DigitalAssetsAdapter(this)

}
