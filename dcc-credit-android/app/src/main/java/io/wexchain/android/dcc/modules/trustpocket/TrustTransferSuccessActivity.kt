package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferSuccessBinding

class TrustTransferSuccessActivity : BindActivity<ActivityTrustTransferSuccessBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btDone.onClick {

            ActivityCollector.finishActivity(TrustTransferActivity::class.java)
            ActivityCollector.finishActivity(TrustTransferCheckActivity::class.java)
            ActivityCollector.finishActivity(TrustTransferActivity::class.java)

            finish()
        }

        binding.tvMobile.text = intent.getStringExtra("mobile")
        binding.tvAddress.text = intent.getStringExtra("address")
        binding.tvAccount.text = intent.getStringExtra("account")

    }
}
