package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.tools.CommonUtils
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

        val chs1 = findViewById<TextView>(R.id.tv_chs1)
        val chs2 = findViewById<TextView>(R.id.tv_chs2)
        val chs3 = findViewById<TextView>(R.id.tv_chs3)

        if (CommonUtils.isRMB()) {
            chs1.visibility = View.VISIBLE
            chs2.visibility = View.VISIBLE
            chs3.visibility = View.VISIBLE
        }
    }
}
