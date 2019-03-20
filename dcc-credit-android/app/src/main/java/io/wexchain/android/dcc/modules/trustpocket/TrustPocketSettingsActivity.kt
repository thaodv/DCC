package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.BaseApplication.Companion.context
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.fingerPrintAvailable
import io.wexchain.android.common.fingerPrintEnrolled
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.cert.SubmitIdActivity
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.switchStatus
import io.wexchain.android.dcc.view.SwitchButton
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketSettingsBinding
import io.wexchain.ipfs.utils.doMain

class TrustPocketSettingsActivity : BindActivity<ActivityTrustPocketSettingsBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_settings

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        val fingerPayStatus = ShareUtils.getBoolean(Extras.SP_TRUST_FINGER_PAY_STATUS, false)

        binding.tvFingerPayStatus.switchStatus = fingerPayStatus

        binding.rlChangePhone.setOnClickListener {
            navigateTo(TrustPocketModifyPhoneActivity::class.java) {
                putExtra("source", "modify")
            }
        }

        binding.rlModifyPwd.setOnClickListener {
            navigateTo(TrustPocketModifyPwdActivity::class.java)
        }

        binding.rlReal.setOnClickListener {
            navigateTo(SubmitIdActivity::class.java)
        }

        binding.fingerPrintAvailable = context.fingerPrintAvailable() && context.fingerPrintEnrolled()

        binding.tvFingerPayStatus.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
            override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                val res = isChecked
                ShareUtils.setBoolean(Extras.SP_TRUST_FINGER_PAY_STATUS, res)
            }
        })

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
