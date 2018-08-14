package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestBinding
import io.wexchain.dcc.databinding.ActivityMyInterestdetailBinding

class MyInterestDetailActivity : BindActivity<ActivityMyInterestdetailBinding>() {

    override val contentLayoutId: Int = R.layout.activity_my_interestdetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initToolbar()
        initclick()

    }

    private fun initclick() {

    }


}
