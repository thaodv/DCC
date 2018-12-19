package io.wexchain.android.dcc.modules.redpacket

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGetRedpacketBinding

class GetRedpacketActivity : BindActivity<ActivityGetRedpacketBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_get_redpacket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.tvRule.setOnClickListener {
            navigateTo(RuleActivity::class.java)
        }


    }
}
