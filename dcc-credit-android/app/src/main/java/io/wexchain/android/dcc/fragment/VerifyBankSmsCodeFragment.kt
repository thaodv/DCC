package io.wexchain.android.dcc.fragment

import android.os.Bundle
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.wexchain.auth.R
import io.wexchain.auth.databinding.FragmentVerifyBankSmsCodeBinding

class VerifyBankSmsCodeFragment : BindFragment<FragmentVerifyBankSmsCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_verify_bank_sms_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_verify_sms_code)
    }
}