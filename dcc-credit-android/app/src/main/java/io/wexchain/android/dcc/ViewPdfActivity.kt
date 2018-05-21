package io.wexchain.android.dcc

import android.os.Bundle
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R

class ViewPdfActivity : BaseCompatActivity() {
    private val pdfUrl
        get() = intent.getStringExtra(Extras.EXTRA_PDF_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)

        loadPdf()
    }

    private fun loadPdf() {
        val url = pdfUrl
        if(pdfUrl.isNullOrEmpty()){
            postOnMainThread {
                finish()
            }
        }else{
            App.get().commonApi.download(url)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({ resp->
                    findViewById<PDFView>(R.id.pdfv).fromStream(resp.byteStream()).load()
                },{
                    toast("协议下载失败")
                    finish()
                })
        }
    }
}
