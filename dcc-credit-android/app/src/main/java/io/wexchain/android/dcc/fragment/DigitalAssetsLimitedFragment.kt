package io.wexchain.android.dcc.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.HomeActivity
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.FragmentDigitalAssetsLimitedBinding
import io.wexchain.digitalwallet.DigitalCurrency

/**
 * Created by sisel on 2018/1/27.
 */
class DigitalAssetsLimitedFragment : BindFragment<FragmentDigitalAssetsLimitedBinding>(), ItemViewClickListener<DigitalCurrency> {
    override val contentLayoutId: Int = R.layout.fragment_digital_assets_limited

    var listLimit = 4

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAssets.layoutManager.isAutoMeasureEnabled = true
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
        (activity as? HomeActivity)?.clickDigitalAssets()
    }

    private val adapter = DigitalAssetsAdapter(this)

}