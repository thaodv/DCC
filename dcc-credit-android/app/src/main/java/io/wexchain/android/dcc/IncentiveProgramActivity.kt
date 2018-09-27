package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class IncentiveProgramActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incentive_program)
        initToolbar()
    }
}
