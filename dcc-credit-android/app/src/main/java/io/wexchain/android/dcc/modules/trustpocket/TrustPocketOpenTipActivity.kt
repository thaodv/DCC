package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.widget.Button
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.dcc.R

class TrustPocketOpenTipActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_pocket_open_tip)
        initToolbar()
        title = getString(R.string.trust_pocket)

        findViewById<Button>(R.id.bt_open).setOnClickListener {
            navigateTo(TrustPocketOpenStep1Activity::class.java)
            finish()
        }

    }
}
