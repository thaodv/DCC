package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.os.SystemClock
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.vm.TrustPocketOpenVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketModifyPhoneBindBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class TrustPocketModifyPhoneBindActivity : BindActivity<ActivityTrustPocketModifyPhoneBindBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_modify_phone_bind

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

        binding.btSubmit.setOnClickListener {

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
                    App.get().marketingApi.changeValidateSmsCode(it, mobile, code)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 换绑成功
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        navigateTo(TrustChangPhoneSuccessActivity::class.java)
                        finish()
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun requestReSendSmsCode(mobile: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.changeSendSmsCode(it, mobile)
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
