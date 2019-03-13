package io.wexchain.android.dcc.modules.trustpocket

import android.content.Intent
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustRechargeBinding
import io.wexchain.ipfs.utils.doMain

class TrustRechargeActivity : BindActivity<ActivityTrustRechargeBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_recharge

    private val mCode get() = intent.getStringExtra("code")
    private val mUrl get() = intent.getStringExtra("url")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        getDepositWallet(mCode)

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, SearchAreaActivity::class.java),
                    RequestCodes.CHOOSE_DIAL_CODE
            )
        }
    }

    fun getDepositWallet(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getDepositWallet(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.address = it.address


                }, {
                    toast(it.message.toString())
                })
    }

}
