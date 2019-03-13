package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class TrustRechargeRecordsActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_recharge_records)
        initToolbar()
    }
}
