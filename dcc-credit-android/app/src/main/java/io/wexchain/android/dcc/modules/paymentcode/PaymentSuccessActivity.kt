package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.modules.home.HomeActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity : BindActivity<ActivityPaymentSuccessBinding>() {

    private val title get() = intent.getStringExtra("title")
    private val id get() = intent.getStringExtra("id")
    private val amount get() = intent.getStringExtra("amount")
    private val assetCode get() = intent.getStringExtra("assetCode")

    override val contentLayoutId: Int
        get() = R.layout.activity_payment_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btDone.onClick {
            navigateTo(HomeActivity::class.java)
        }

        binding.tvTitle.text = title
        binding.tvId.text = id
        binding.tvAmount.text = "$amount $assetCode"

    }
}
