package io.wexchain.android.dcc.modules.bsx

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxMarketBinding

class BsxMarketActivity : BindActivity<ActivityBsxMarketBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_market

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.ivNext.setOnClickListener { navigateTo(BsxHoldingActivity::class.java) }

    }
}
