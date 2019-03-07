package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.os.SystemClock
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.TrustPocketOpenVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketOpenStep1Binding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class TrustPocketOpenStep1Activity : BindActivity<ActivityTrustPocketOpenStep1Binding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_open_step1

    var smsUpTimeStamp: Long = 0

    var phoneNum: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        phoneNum = binding.etPhone.text.toString()
        smsUpTimeStamp = if (binding.etCheckCode.text.toString() == "") 0 else binding.etCheckCode.text.toString().toLong()


        val viewModel = getViewModel<TrustPocketOpenVm>().apply {
            phoneNo.set(phoneNum)
            upTimeStamp.value = smsUpTimeStamp
        }

        binding.vm = viewModel
        binding.btGet.setOnClickListener {
            viewModel.code.set("")
            val phoneNum = binding.etPhone.text.toString()
            if ("" == phoneNum) {
                toast("请输入手机号码")
            } else {
                requestReSendSmsCode(phoneNum)
            }
        }

        binding.btNext.setOnClickListener {

            val checkCodeValue = binding.etCheckCode.text.toString()

            if ("" == checkCodeValue) {
                toast("请输入验证码")
            } else {
                checkCode(binding.etPhone.text.toString(), checkCodeValue)
            }
        }
    }

    private fun checkCode(mobile: String, code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.validateSmsCode(it, mobile, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    it.pubKey
                    it.salt
                    ShareUtils.setString(Extras.SP_TRUST_PUBKEY, it.pubKey)
                    ShareUtils.setString(Extras.SP_TRUST_MOBILE_PHONE, mobile)

                    navigateTo(TrustPocketOpenStep2Activity::class.java) {
                        putExtra("salt", it.salt)
                    }
                    finish()
                }, {
                    toast(it.message.toString())
                })
    }

    private fun requestReSendSmsCode(mobile: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.sendSmsCode(it, mobile)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        toast("已发验证码")
                        smsUpTimeStamp = SystemClock.uptimeMillis()
                        binding.vm?.upTimeStamp?.value = smsUpTimeStamp
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }
}
