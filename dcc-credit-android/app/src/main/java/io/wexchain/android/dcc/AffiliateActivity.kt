package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.setWindowExtended
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityAffiliateBinding

class AffiliateActivity : BindActivity<ActivityAffiliateBinding>() {
    override val contentLayoutId: Int = R.layout.activity_affiliate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()
    }
}
