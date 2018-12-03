package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanInfoBinding

class LoanInfoActivity : BindActivity<ActivityLoanInfoBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_loan_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
