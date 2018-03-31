package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivitySubmitBankCardBinding

class SubmitBankCardActivity : BindActivity<ActivitySubmitBankCardBinding>() {
    override val contentLayoutId: Int = R.layout.activity_submit_bank_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}
