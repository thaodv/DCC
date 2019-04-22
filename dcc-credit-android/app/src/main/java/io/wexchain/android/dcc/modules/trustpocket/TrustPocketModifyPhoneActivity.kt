package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.os.SystemClock
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.TrustPocketOpenVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketModifyPhoneBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class TrustPocketModifyPhoneActivity : BindActivity<ActivityTrustPocketModifyPhoneBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_modify_phone

    private val mSource get() = intent.getStringExtra("source")

    var phoneNum = ""

    var smsUpTimeStamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        if (mSource == "modify") {
            title = getString(R.string.trust_pocket_modify_bind_phone)
        } else {
            title = getString(R.string.trust_forget_pwd)
        }

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getMobileUser(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    phoneNum = it.mobile
                    //binding.tvPhone.text = CommonUtils.phone(it.mobile)
                }, {
                    toast(it.message.toString())
                })

        smsUpTimeStamp = if (binding.etCheckCode.text.toString() == "") 0 else binding.etCheckCode.text.toString().toLong()

        binding.tvPhone.text = CommonUtils.phone(phoneNum)

        val viewModel = getViewModel<TrustPocketOpenVm>().apply {
            upTimeStamp.value = smsUpTimeStamp
        }

        binding.vm = viewModel
        binding.btGet.setOnClickListener {
            viewModel.code.set("")
            if (mSource == "modify") {
                requestReSendSmsCode(phoneNum)
            } else {
                createPayPwdSecurityContext(phoneNum)
            }
        }

        binding.btNext.setOnClickListener {

            val checkCodeValue = binding.etCheckCode.text.toString()

            if ("" == checkCodeValue) {
                toast(getString(R.string.trust_pocket_open_hint2))
            } else {
                if (mSource == "modify") {
                    checkCode(phoneNum, checkCodeValue)
                } else {
                    validateSmsCode2(phoneNum, checkCodeValue)
                }
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
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        navigateTo(TrustPocketModifyPhoneBindActivity::class.java)
                        finish()
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun createPayPwdSecurityContext(phoneNum: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.createPayPwdSecurityContext(it)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        sendSmsCode2(phoneNum)
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun validateSmsCode2(mobile: String, code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.validateSmsCode2(it, mobile, code)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        prepareInputPwd()
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun sendSmsCode2(phoneNum: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.sendSmsCode2(it, phoneNum)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        toast(getString(R.string.hasSend))
                        smsUpTimeStamp = SystemClock.uptimeMillis()
                        binding.vm?.upTimeStamp?.value = smsUpTimeStamp
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun prepareInputPwd() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.prepareInputPwd(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    //validatePaymentPassword(it.pubKey, it.salt)
                    ShareUtils.setString(Extras.SP_TRUST_PUBKEY, it.pubKey)
                    navigateTo(TrustPocketOpenStep2Activity::class.java) {
                        putExtra("salt", it.salt)
                        putExtra("use", "forget")
                    }
                    finish()
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
                        toast(getString(R.string.hasSend))
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
