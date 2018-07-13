package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.dialog_check_update.*
import kotlinx.android.synthetic.main.dialog_download.*
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Failed
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import zlc.season.rxdownload3.extension.ApkInstallExtension
import zlc.season.rxdownload3.helper.dispose

/**
 * Created by liuyang on 2018/7/13.
 */
class UpgradeDialog(context: Context) : Dialog(context) {

    private var disposable: Disposable? = null

    fun createCheckDialog(newvs: String, body: String, onCancle: (() -> Unit), onConfirm: (() -> Unit)) {
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_check_update, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.7).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)

        check_btn_tag.visibility = View.VISIBLE
        check_upgrade_cancle.setOnClickListener {
            onCancle.invoke()
        }

        check_upgrade_confirm.setOnClickListener {
            onConfirm.invoke()
        }
        check_upgrade_title.text = "检测到v$newvs 版本，是否下载更新"
        check_upgrade_body.text = body
        check_upgrade_vs.text = "新功能 v$newvs"

        show()
    }

    fun createHomeDialog(newvs: String, body: String,  onConfirm: (() -> Unit)) {
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_check_update, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.7).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)

        home_upgrade_confirm.visibility = View.VISIBLE
        check_upgrade_title.text = "当前版本已无法正常使用，请立即更新"
        check_upgrade_body.text = body
        check_upgrade_vs.text = "新功能 v$newvs"

        home_upgrade_confirm.setOnClickListener {
            onConfirm.invoke()
        }
        show()
    }

    fun crateDownloadDialog(mission: Mission){
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_download, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.8).toInt()
        dialogWindow.attributes = lp
//        window.setBackgroundDrawableResource(R.drawable.background_holding)

        show()
        downloadApk(mission)
    }


    private fun downloadApk(mission: Mission) {
        disposable = RxDownload.create(mission, autoStart = true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { status ->
                    if(status is Failed){
                        Log.e("Failed",status .throwable.message)
                    }
                    setProgress(status)
                    if (status is Succeed){
                        RxDownload.extension(mission, ApkInstallExtension::class.java).subscribe()
                    }
                }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dispose(disposable)
    }

    private fun setProgress(status: Status) {
        progressBar.max = status.totalSize.toInt()
        progressBar.progress = status.downloadSize.toInt()

        percent.text = status.percent()
        size.text = status.formatString()
    }
}