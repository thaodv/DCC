package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.widget.Button
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class TrustChangPhoneSuccessActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_chang_phone_success)

        findViewById<Button>(R.id.bt_done).setOnClickListener {
            finish()
        }
    }
}
