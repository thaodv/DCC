package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentReceiptDetailTimeoutBinding

class PaymentReceiptDetailTimeoutActivity : BindActivity<ActivityPaymentReceiptDetailTimeoutBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_receipt_detail_timeout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }


    }
}
