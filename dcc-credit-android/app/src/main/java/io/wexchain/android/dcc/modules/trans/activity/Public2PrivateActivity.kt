package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPublic2PrivateBinding

class Public2PrivateActivity : BindActivity<ActivityPublic2PrivateBinding>() {

    override val contentLayoutId: Int = R.layout.activity_public2_private

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
