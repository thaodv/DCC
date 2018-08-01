package io.wexchain.android.dcc.fragment

import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.vm.VerifyBankCardSmsCodeVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentVerifyBankSmsCodeBinding

class VerifyBankSmsCodeFragment : BindFragment<FragmentVerifyBankSmsCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_verify_bank_sms_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel<VerifyBankCardSmsCodeVm>().apply {
            phoneNo.set(phoneNum)
            upTimeStamp.value = smsUpTimeStamp
        }
        binding.vm = viewModel
        binding.btnSendSmsCode.setOnClickListener {
            listener?.requestReSendSmsCode()
            viewModel.code.set("")
        }
        binding.btnSubmitCode.setOnClickListener {
            viewModel.code.get()?.let {
                //todo check
                listener?.onSubmitSmsCode(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_verify_sms_code)
    }

    var phoneNum:String
        get() = arguments?.getString(Extras.EXTRA_BANK_CARD_PHONE_NUM)?:""
        set(value) {
            if(arguments == null){
                    arguments = Bundle()
                }
            arguments!!.putString(Extras.EXTRA_BANK_CARD_PHONE_NUM, value)
        }

    var smsUpTimeStamp:Long
        get() = arguments?.getLong(Extras.EXTRA_BANK_CARD_VERIFY_SMS_UP_TIME_STAMP,0L)?:0L
        set(value) {
            if(arguments == null){
                    arguments = Bundle()
                }
            arguments!!.putLong(Extras.EXTRA_BANK_CARD_VERIFY_SMS_UP_TIME_STAMP, value)
            if (isBindingInitialized) {
                binding.vm?.upTimeStamp?.value = value
            }
        }

    private var listener:Listener? = null

    interface Listener{
        fun onSubmitSmsCode(code: String)
        fun requestReSendSmsCode()
    }

    companion object {
        fun create(listener: Listener): VerifyBankSmsCodeFragment {
            val fragment = VerifyBankSmsCodeFragment()
            fragment.listener = listener
            return fragment
        }
    }
}