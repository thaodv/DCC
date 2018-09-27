package io.wexchain.android.dcc.modules.repay

import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class RepayingActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repaying)
        initToolbar(true)
    }
}
