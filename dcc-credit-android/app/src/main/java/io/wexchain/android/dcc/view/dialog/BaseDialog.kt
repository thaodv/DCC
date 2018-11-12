package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.wexchain.android.common.installApk
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.tools.getString
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.dialog_check_update.*
import kotlinx.android.synthetic.main.dialog_download.*
import org.jetbrains.anko.toast
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Failed
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import zlc.season.rxdownload3.helper.dispose
import java.io.File

/**
 * Created by liuyang on 2018/7/13.
 */
class BaseDialog(context: Context) : Dialog(context) {

    private var disposable: Disposable? = null
    private var currentStatus = Status()
    private lateinit var mission: Mission

    fun createCheckDialog(newvs: String, body: String): BaseDialog {
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
            onCancleStub.invoke()
        }

        check_upgrade_confirm.setOnClickListener {
            onConfirmStub.invoke()
        }
        check_upgrade_title.text = "检测到v$newvs 版本，是否下载更新"
        check_upgrade_body.text = body
        check_upgrade_vs.text = "新功能 v$newvs"

        show()
        return this
    }

    fun removePassportDialog(titletxt: String? = null, messagetxt: String? = null, lefttxt: String? = null, righttxt: String? = null): BaseDialog {
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_remove_passport, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.8).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)

        val cancel = view.findViewById<TextView>(R.id.dialog_cancle)
        val confirm = view.findViewById<TextView>(R.id.dialog_confirm)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val message = view.findViewById<TextView>(R.id.dialog_message)

        titletxt?.let {
            title.text = it
        }
        messagetxt?.let {
            message.text = it
        }
        lefttxt?.let {
            cancel.text = it
        }
        righttxt?.let {
            confirm.text = it
        }

        cancel.setOnClickListener {
            onCancleStub.invoke()
        }

        confirm.setOnClickListener {
            onConfirmStub.invoke()
        }
        view.findViewById<TextView>(R.id.dialog_dismiss).onClick {
            dismiss()
        }
        show()
        return this
    }

    fun BoundWechatDialog(): BaseDialog {
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_bound_wechat, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.8).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)

        view.findViewById<ImageView>(R.id.bound_close).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.bound_confirm).setOnClickListener {
            onConfirmStub.invoke()
            dismiss()
        }
        show()
        return this
    }

    private var onCancleStub: () -> Unit = {}
    private var onConfirmStub: () -> Unit = {}

    fun onClick(onCancle: () -> Unit = onCancleStub, onConfirm: () -> Unit) {
        onCancleStub = onCancle
        onConfirmStub = onConfirm
    }

    fun createHomeDialog(newvs: String, body: String): BaseDialog {
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_check_update, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.7).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)

        home_upgrade_confirm.visibility = View.VISIBLE
        check_upgrade_title.text = getString(R.string.please_download_and_update)
        check_upgrade_body.text = body
        check_upgrade_vs.text = "新功能 v$newvs"

        home_upgrade_confirm.setOnClickListener {
            onConfirmStub.invoke()
        }
        show()
        return this
    }

    fun crateDownloadDialog(mission: Mission) {
        this.mission = mission
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_download, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.8).toInt()
        dialogWindow.attributes = lp

        show()
        downloadApk()
    }


    private fun downloadApk() {
        disposable = RxDownload.create(mission, autoStart = true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentStatus = it
                    setProgress(currentStatus)
                    if (currentStatus is Failed) {
                        error()
                    }
                    if (currentStatus is Succeed) {
                        val file = File(mission.savePath, mission.saveName)
                        if (file.exists()) {
                            context.installApk(file)
                        } else {
                            error()
                        }
                    }
                }
    }

    private fun error() {
        context.toast("下载出现异常")
        dismiss()
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