package io.wexchain.android.dcc.fragment

import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentVerifyCarrierSmsCodeBinding

class VerifyCarrierSmsCodeFragment: BindFragment<FragmentVerifyCarrierSmsCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_verify_carrier_sms_code

    private var listener:Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = ""//clear
        binding.btnSubmitCode.setOnClickListener {
            binding.code?.let {
                listener?.onSubmitSmsCode(it)
            }
        }
    }

    interface Listener{
        fun onSubmitSmsCode(code:String)
    }

    companion object {
        fun create(listener: Listener): VerifyCarrierSmsCodeFragment {
            val fragment = VerifyCarrierSmsCodeFragment()
            fragment.listener = listener
            return fragment
        }
    }
}