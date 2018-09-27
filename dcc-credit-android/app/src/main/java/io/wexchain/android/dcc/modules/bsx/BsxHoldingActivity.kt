package io.wexchain.android.dcc.modules.bsx

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxHoldingBinding

class BsxHoldingActivity : BindActivity<ActivityBsxHoldingBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_holding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        binding.tvBuyAgain.setOnClickListener { finish() }
    }
}
