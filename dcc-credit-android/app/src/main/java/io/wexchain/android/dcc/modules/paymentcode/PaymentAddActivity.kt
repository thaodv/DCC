package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentAddBinding

class PaymentAddActivity : BindActivity<ActivityPaymentAddBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_add

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()


    }
}
