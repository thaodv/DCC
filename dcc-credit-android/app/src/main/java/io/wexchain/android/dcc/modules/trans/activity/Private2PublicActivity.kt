package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPrivate2PublicBinding

class Private2PublicActivity : BindActivity<ActivityPrivate2PublicBinding>() {

    override val contentLayoutId: Int = R.layout.activity_private2_public

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

}
