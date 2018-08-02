package io.wexchain.android.dcc.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.transitionBundle
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.DigitalAssetsActivity
import io.wexchain.android.dcc.HomeActivity
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentDigitalAssetsLimitedBinding
import io.wexchain.digitalwallet.DigitalCurrency

/**
 * Created by sisel on 2018/1/27.
 */
class DigitalAssetsLimitedFragment : BindFragment<FragmentDigitalAssetsLimitedBinding>(), ItemViewClickListener<DigitalCurrency> {

    override val contentLayoutId: Int = R.layout.fragment_digital_assets_limited

    var listLimit = 4

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAssets.isNestedScrollingEnabled = false
        binding.rvAssets.adapter = adapter
        val assetsVm: DigitalAssetsVm = getViewModel()
        adapter.assetsVm = assetsVm
        binding.assets = assetsVm
        assetsVm.ensureHolderAddress(this)
        assetsVm.assets.observe(this, Observer {
            val limit = listLimit
            val list = if (it != null && limit > 0) {
                it.subList(0, minOf(limit, it.size))
            } else it
            adapter.setList(list)
        })
    }

    override fun onResume() {
        super.onResume()
        binding.assets!!.updateHoldingAndQuote()
    }

    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        item ?: return
        val t = (activity as? HomeActivity)
        if (t != null) {
            if (App.get().passportRepository.passportExists) {
                t.navigateTo(DigitalAssetsActivity::class.java, t.transitionBundle(
                        Transitions.create(t.findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
                        , Transitions.create(t.findViewById(R.id.assets_amount_label), Transitions.DIGITAL_ASSETS_AMOUNT_LABEL)
                        , Transitions.create(t.findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
                ))
            } else {
                t.showIntroWalletDialog()
            }
        }
    }

    private val adapter = DigitalAssetsAdapter(this)

}
