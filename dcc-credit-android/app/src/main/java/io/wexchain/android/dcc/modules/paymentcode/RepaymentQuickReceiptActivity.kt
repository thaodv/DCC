package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityRepaymentQuickReceiptBinding

class RepaymentQuickReceiptActivity : BindActivity<ActivityRepaymentQuickReceiptBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_repayment_quick_receipt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        

    }
}
