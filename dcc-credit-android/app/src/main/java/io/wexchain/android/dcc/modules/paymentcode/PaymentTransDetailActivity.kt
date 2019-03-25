package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentReceiptDetailBinding

class PaymentTransDetailActivity : BindActivity<ActivityPaymentReceiptDetailBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_trans_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }


    }
}
