package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentTransRecordsBinding

class PaymentTransRecordsActivity : BindActivity<ActivityPaymentTransRecordsBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_trans_records

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
