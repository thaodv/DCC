package io.wexchain.android.dcc.modules.bsx

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxHoldingDetailBinding

class BsxHoldingDetailActivity : BindActivity<ActivityBsxHoldingDetailBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_holding_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
    }
}
