package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTransDetailBinding

class TransDetailActivity : BindActivity<ActivityTransDetailBinding>() {

    override val contentLayoutId: Int = R.layout.activity_trans_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

    }
}
