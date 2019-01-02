package io.wexchain.android.dcc.modules.cert

import android.os.Bundle
import android.widget.TextView
import io.wexchain.android.common.atLeastCreated
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R

class CmCertDataActivity : BaseCompatActivity() {

    private val orderId
        get() = intent.getLongExtra(Extras.EXTRA_CERT_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cm_cert_data)
        initToolbar()
        showCmCertData()
    }

    private fun showCmCertData() {
        val id = orderId
        if (id == -1L) {
            postOnMainThread {
                finish()
            }
        } else {
            CertOperations.getCmLogData(id)
                .map {
                    it.toString(Charsets.UTF_8)
                }
                .subscribe({
                    atLeastCreated {
                        setCertData(it)
                    }
                }, {
                    atLeastCreated {
                        toast("读取认证报告错误$id")
                        finish()
                    }
                })
        }
    }

    private fun setCertData(data: String?) {
        findViewById<TextView>(R.id.tv_data).text = data
    }
}
