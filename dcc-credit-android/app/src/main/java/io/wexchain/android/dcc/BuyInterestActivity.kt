package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBuyInterestBinding
import io.wexchain.dcc.databinding.ActivityMyInterestBinding
import io.wexchain.dcc.databinding.ActivityMyInterestdetailBinding

class BuyInterestActivity : BindActivity<ActivityBuyInterestBinding>() {

    override val contentLayoutId: Int = R.layout.activity_buy_interest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initToolbar()
        initclick()

    }

    private fun initclick() {

    }


}
