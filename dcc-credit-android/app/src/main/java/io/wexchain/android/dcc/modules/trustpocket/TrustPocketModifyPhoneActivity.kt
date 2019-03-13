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
import io.wexchain.android.dcc.modules.cert.SubmitIdActivity
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.TrustPocketOpenVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketModifyPhoneBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class TrustPocketModifyPhoneActivity : BindActivity<ActivityTrustPocketModifyPhoneBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_modify_phone

    private val mSource get() = intent.getStringExtra("source")

    val phoneNum = ShareUtils.getString(Extras.SP_TRUST_MOBILE_PHONE)

    var smsUpTimeStamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        if (mSource == "modify") {
            title = "修改绑定手机"
        } else {
            title = "忘记密码"
        }


        smsUpTimeStamp = if (binding.etCheckCode.text.toString() == "") 0 else binding.etCheckCode.text.toString().toLong()

        binding.tvPhone.text = CommonUtils.phone(phoneNum)

        val viewModel = getViewModel<TrustPocketOpenVm>().apply {
            upTimeStamp.value = smsUpTimeStamp
        }

        binding.vm = viewModel
        binding.btGet.setOnClickListener {
            viewModel.code.set("")
            requestReSendSmsCode(phoneNum)
        }

        binding.btNext.setOnClickListener {

            val checkCodeValue = binding.etCheckCode.text.toString()

            if ("" == checkCodeValue) {
                toast("请输入验证码")
            } else {
                checkCode(phoneNum, checkCodeValue)
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
                        if (mSource == "modify") {
                            navigateTo(TrustPocketModifyPhoneBindActivity::class.java)
                            finish()
                        } else {
                            checkHasReal()
                        }
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun checkHasReal() {

        App.get().chainGateway.getCertData(App.get().passportRepository.getCurrentPassport()!!.address, ChainGateway.BUSINESS_ID)
                .check()
                .doMain()
                .withLoading()
                .subscribe({
                    // 未实名
                    if ("" == it.content.digest1) {
                        //navigateTo()
                        prepareInputPwd()
                    } else {
                        navigateTo(SubmitIdActivity::class.java)
                        finish()
                    }
                }, {

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
                        putExtra("use","forget")
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
