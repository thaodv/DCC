package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityDccExchangeBinding

class DccExchangeActivity : BindActivity<ActivityDccExchangeBinding>() {
    override val contentLayoutId: Int = R.layout.activity_dcc_exchange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()
    }
}
