package io.wexchain.android.dcc

import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.dcc.vm.SelectOptions
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivitySubmitBankCardBinding
import io.wexchain.dccchainservice.domain.BankCodes

class SubmitBankCardActivity : BindActivity<ActivitySubmitBankCardBinding>() {
    override val contentLayoutId: Int = R.layout.activity_submit_bank_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.inputBank!!.vm = SelectOptions().apply {
            optionTitle.set("开户行")
            options.set(BankCodes.banks)
        }
    }
}
