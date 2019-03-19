package io.wexchain.android.dcc.modules.digestpocket

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.resultOk
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsChooseAdapter
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityChooseDigestBinding
import io.wexchain.digitalwallet.DigitalCurrency

class ChooseDigestActivity : BindActivity<ActivityChooseDigestBinding>(), ItemViewClickListener<DigitalCurrency> {
    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        resultOk {
            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, item!!)
        }
    }

    private val adapter = DigitalAssetsChooseAdapter(this)

    override val contentLayoutId: Int get() = R.layout.activity_choose_digest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        App.get().assetsRepository.displayCurrencies.observe(this, Observer {
            adapter.setList(it)
        })

        binding.rvAssets.adapter = adapter
    }
}
