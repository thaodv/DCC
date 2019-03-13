package worhavah.tongniucertmodule

import android.os.Bundle
import android.widget.TextView
import com.githang.statusbar.StatusBarCompat
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.postOnMainThread
import worhavah.certs.tools.CertOperations
import worhavah.mobilecertmodule.R

class TNCertDataActivity : BaseCompatActivity() {

    private val orderId
        get() = intent.getLongExtra(Extras.EXTRA_CERT_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tncm_cert_data)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white))
        initToolbar(true, true)
        showCmCertData()
    }

    private fun showCmCertData() {
        val id = orderId
        if (id == -1L) {
            postOnMainThread {
                finish()
            }
        } else {
            setCertData(CertOperations.certPrefs.certTNLogData.get())
        }
    }

    private fun setCertData(data: String?) {
        findViewById<TextView>(R.id.tv_data).text = data
    }

}
