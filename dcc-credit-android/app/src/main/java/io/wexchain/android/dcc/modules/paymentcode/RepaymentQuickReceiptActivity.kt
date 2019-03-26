package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import android.widget.TextView
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityRepaymentQuickReceiptBinding

class RepaymentQuickReceiptActivity : BindActivity<ActivityRepaymentQuickReceiptBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_repayment_quick_receipt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        findViewById<TextView>(R.id.tv_add).onClick {
            navigateTo(PaymentAddActivity::class.java)
        }


    }
}
