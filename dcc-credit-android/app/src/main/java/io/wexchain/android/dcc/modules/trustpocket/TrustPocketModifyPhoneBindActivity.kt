package io.wexchain.android.dcc.modules.trustpocket

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import com.google.gson.Gson
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.AreaCode
import io.wexchain.android.common.constant.AreaCodeBean
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.vm.TrustPocketOpenVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketModifyPhoneBindBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class TrustPocketModifyPhoneBindActivity : BindActivity<ActivityTrustPocketModifyPhoneBindBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_modify_phone_bind

    var smsUpTimeStamp: Long = 0

    var phoneNum: String = ""

    var mDialCode: String = "86"

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

        val country = resources.configuration.locale.country

        LogUtils.i("country", country)

        val mDatas: AreaCodeBean = Gson().fromJson(AreaCode.res, AreaCodeBean::class.java)
        val res = mDatas.res

        for (item in res) {
            if (item.country_code == country) {
                mDialCode = item.dial_code
            }
        }

        LogUtils.i("dialCode", mDialCode)
        binding.tvArea.text = "+$mDialCode"

        binding.tvArea.onClick {
            startActivityForResult(
                    Intent(this, SearchAreaActivity::class.java),
                    RequestCodes.CHOOSE_DIAL_CODE
            )

        }

        binding.btGet.setOnClickListener {
            viewModel.code.set("")
            val phoneNum = binding.etPhone.text.toString()
            if ("" == phoneNum) {
                toast(getString(R.string.please_input_phone_no))
            } else {
                requestReSendSmsCode("+$mDialCode$phoneNum")
            }
        }

        binding.btSubmit.setOnClickListener {

            val checkCodeValue = binding.etCheckCode.text.toString()

            if ("" == checkCodeValue) {
                toast(getString(R.string.trust_pocket_open_hint2))
            } else {
                updateMobile("+" + mDialCode + binding.etPhone.text.toString(), checkCodeValue)
            }
        }
    }

    private fun updateMobile(mobile: String, code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.updateMobile(it, mobile, code)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_DIAL_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mDialCode = data!!.getStringExtra("dialCode")
                    binding.tvArea.text = "+$mDialCode"
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
