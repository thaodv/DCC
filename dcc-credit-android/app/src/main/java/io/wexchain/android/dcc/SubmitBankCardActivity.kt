package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.fragment.InputBankCardInfoFragment
import io.wexchain.android.dcc.fragment.VerifyBankSmsCodeFragment
import io.wexchain.auth.R

class SubmitBankCardActivity : BaseCompatActivity() {

    private val inputBankCardInfoFragment by lazy { InputBankCardInfoFragment() }

    private val verifyBankSmsCodeFragment by lazy { VerifyBankSmsCodeFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        enterStep(STEP_INPUT_BANK_CARD_INFO)
    }

    private fun enterStep(step: Int) {
        when(step){
            STEP_INPUT_BANK_CARD_INFO -> replaceFragment(inputBankCardInfoFragment,R.id.fl_container)
            STEP_VERIFY_BANK_CARD_SMS_CODE -> replaceFragment(verifyBankSmsCodeFragment,R.id.fl_container,backStackStateName = "step_$step")
        }
    }

    companion object {
        const val STEP_INPUT_BANK_CARD_INFO = 0
        const val STEP_VERIFY_BANK_CARD_SMS_CODE = 1
    }
}
