package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestBinding

class MyInterestActivity : BindActivity<ActivityMyInterestBinding>() {

    override val contentLayoutId: Int = R.layout.activity_my_interest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initclick()

    }

    private fun initclick() {
         binding.tvback.setOnClickListener {
             finish()
         }
        binding.tvHave.setOnClickListener {
            navigateTo(MyInterestDetailActivity::class.java)
        }
        binding.tvBuyit.setOnClickListener {
            navigateTo(BuyInterestActivity::class.java)
        }





    }


}
