package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.cert.SubmitIdActivity
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketSettingsBinding
import io.wexchain.ipfs.utils.doMain

class TrustPocketSettingsActivity : BindActivity<ActivityTrustPocketSettingsBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.rlChangePhone.setOnClickListener {
            navigateTo(TrustPocketModifyPhoneActivity::class.java){
                putExtra("source", "modify")
            }
        }

        binding.rlModifyPwd.setOnClickListener {
            navigateTo(TrustPocketModifyPwdActivity::class.java)
        }

        binding.rlReal.setOnClickListener {
            navigateTo(SubmitIdActivity::class.java)
        }

    }

    override fun onResume() {
        super.onResume()
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getMobileUser(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvPhoneNum.text = it.mobile
                    ShareUtils.setString(Extras.SP_TRUST_MOBILE_PHONE, it.mobile)
                }, {
                    toast(it.message.toString())
                })
    }
}
