package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustWithdrawSuccessBinding

class TrustWithdrawSuccessActivity : BindActivity<ActivityTrustWithdrawSuccessBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_withdraw_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btDone.onClick {

            ActivityCollector.finishActivity(TrustChooseCoinActivity::class.java)
            ActivityCollector.finishActivity(TrustWithdrawActivity::class.java)

            finish()
        }

        binding.tvAddress.text = intent.getStringExtra("address")
        binding.tvAccount.text = intent.getStringExtra("account")

    }
}
