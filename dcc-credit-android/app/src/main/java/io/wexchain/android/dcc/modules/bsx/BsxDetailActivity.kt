package io.wexchain.android.dcc.modules.bsx

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxDetailBinding

class BsxDetailActivity : BindActivity<ActivityBsxDetailBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

    }
}
