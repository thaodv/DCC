package io.wexchain.android.dcc.modules.cashloan

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyLoanBinding

class MyLoanActivity : BindActivity<ActivityMyLoanBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_my_loan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
