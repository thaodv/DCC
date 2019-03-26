package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R

class PaymentUnOpenActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_un_open)
        initToolbar()

        findViewById<TextView>(R.id.tv_use).onClick {
            navigateTo(RepaymentQuickReceiptActivity::class.java)
        }

    }
}
