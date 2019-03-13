package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class TrustWithdrawActivity : BaseCompatActivity() {

    private val mCode get() = intent.getStringExtra("code")
    private val mUrl get() = intent.getStringExtra("url")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_withdraw)
        initToolbar()
    }
}
