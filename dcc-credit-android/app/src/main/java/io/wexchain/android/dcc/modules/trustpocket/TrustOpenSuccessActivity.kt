package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.modules.cert.SubmitIdActivity
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R

class TrustOpenSuccessActivity : BaseCompatActivity() {

    private val mMobile get() = intent.getStringExtra("mobile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_open_success)

        val btDone = findViewById<Button>(R.id.bt_done)
        val tvRealTip = findViewById<TextView>(R.id.tv_real_tip)

        val btRealName = findViewById<Button>(R.id.bt_realname)

        if (mMobile.startsWith("+86")) {
            // 已经实名认证
            if (CertOperations.getCertStatus(CertificationType.ID) == UserCertStatus.DONE) {
                btDone.text = getString(R.string.done)
                tvRealTip.visibility = View.GONE
                btRealName.visibility = View.GONE
            } else {
                btDone.text = getString(R.string.skip)
                tvRealTip.visibility = View.VISIBLE
                btRealName.visibility = View.VISIBLE
            }
        } else {
            btDone.text = getString(R.string.done)
            tvRealTip.visibility = View.GONE
            btRealName.visibility = View.GONE
        }

        btRealName.setOnClickListener {
            if (mMobile.startsWith("+86")) {
                // 已经实名认证
                if (CertOperations.getCertStatus(CertificationType.ID) == UserCertStatus.DONE) {
                    finish()
                } else {
                    navigateTo(SubmitIdActivity::class.java)
                    finish()
                }
            } else {
                finish()
            }
        }

        btDone.setOnClickListener {
            finish()
        }
    }
}
