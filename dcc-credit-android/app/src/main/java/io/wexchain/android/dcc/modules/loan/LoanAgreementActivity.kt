package io.wexchain.android.dcc.modules.loan

import android.os.Bundle
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R

class LoanAgreementActivity : BaseCompatActivity() {

    private val loanOrderId
        get() = intent.getLongExtra(Extras.EXTRA_LOAN_CHAIN_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_agreement)
        loadPdf()
    }

    private fun loadPdf() {
        val orderId = loanOrderId
        if (orderId == -1L) {
            toast("id无效")
            postOnMainThread {
                finish()
            }
        } else {
            ScfOperations.loadContractPdf(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe ({ bytes->
                    findViewById<PDFView>(R.id.pdfv).fromBytes(bytes).load()
                },{
                    toast("合同下载失败")
                    finish()
                })
        }
    }
}
