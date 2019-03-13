package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketHomeBinding

class TrustPocketHomeActivity : BindActivity<ActivityTrustPocketHomeBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.ivBack.onClick {
            finish()
        }

        binding.llIn.onClick {
            navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "recharge")
            }
        }
        binding.llOut.onClick {
            navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "withdraw")
            }
        }
    }
}
