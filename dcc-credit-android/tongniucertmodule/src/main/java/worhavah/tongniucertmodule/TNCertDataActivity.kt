package worhavah.tongniucertmodule

import android.os.Bundle
import android.widget.TextView
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.Extras2
import worhavah.certs.tools.CertOperations
import worhavah.mobilecertmodule.R

class TNCertDataActivity : BaseCompatActivity() {

    private val orderId
        get() = intent.getLongExtra(Extras2.EXTRA_CERT_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tncm_cert_data)
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

            setCertData( CertOperations.certPrefs.certTNLogData.get( ))
          /*  CertOperations.getTNLogData(id)
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
                })*/
        }
    }

    private fun setCertData(data: String?) {
        findViewById<TextView>(R.id.tv_data).text = data
    }




}
