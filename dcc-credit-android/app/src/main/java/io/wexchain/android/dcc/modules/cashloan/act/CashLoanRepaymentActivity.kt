package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashLoanRepaymentBinding

class CashLoanRepaymentActivity : BindActivity<ActivityCashLoanRepaymentBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_cash_loan_repayment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
