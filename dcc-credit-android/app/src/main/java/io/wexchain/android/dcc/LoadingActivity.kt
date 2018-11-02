package io.wexchain.android.dcc

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.atLeastCreated
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.noStatusBar
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.modules.home.HomeActivity
import io.wexchain.android.dcc.tools.PermissionHelper
import io.wexchain.android.dcc.tools.checkXPosed
import io.wexchain.android.dcc.tools.isRoot
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_loading.*
import java.util.concurrent.TimeUnit

class LoadingActivity : BaseCompatActivity() {

    private lateinit var helper: PermissionHelper
    private val WRITE_EXTERNAL_STORAGE_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noStatusBar()
        setContentView(R.layout.activity_loading)
        permission()
    }

    private fun permission() {
        helper = PermissionHelper(this)
        helper.requestPermissions(arrayOf(PermissionHelper.PermissionModel("存储空间", Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "我们需要您允许我们读写你的存储卡，以方便我们临时保存一些数据", WRITE_EXTERNAL_STORAGE_CODE))) {
            safeCcheck()
        }
    }

    private fun safeCcheck() {
        if (isRoot() || checkXPosed()) {
            safe_check.visibility = View.VISIBLE
//            您的手机已被root，这将导致手机运行环境不安全。2.发现危险程序xposed，请先卸载
            safe_ignore.onClick {
                SafeDialog(this)
                        .init()
//                        .setMessage(" ")
                        .setCancelClick {
                            it.dismiss()
                            finish()
                        }
                        .setConfirmClick {
                            it.dismiss()
                            safe_check.visibility = View.INVISIBLE
                            delayedStart()
                        }
            }
            safe_close.onClick {
                finish()
            }
        } else {
            delayedStart()
        }
    }


    class SafeDialog(context: Context) : Dialog(context) {

        fun setMessage(message: String): SafeDialog {
            findViewById<TextView>(R.id.safe_message).text = message
            return this
        }

        fun setCancelClick(click: (SafeDialog) -> Unit): SafeDialog {
            findViewById<TextView>(R.id.safe_cancel).onClick {
                click(this)
            }
            return this
        }

        fun setConfirmClick(click: (SafeDialog) -> Unit): SafeDialog {
            findViewById<TextView>(R.id.safe_confirm).onClick {
                click(this)
            }
            return this
        }

        fun init(): SafeDialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_check_safe, null)
            setContentView(view)
            val dialogWindow = window
            val lp = dialogWindow!!.attributes
            val d = context.resources.displayMetrics
            lp.width = (d.widthPixels * 0.8).toInt()
            lp.height = (d.heightPixels * 0.3).toInt()
            dialogWindow.attributes = lp
            window.setBackgroundDrawableResource(R.drawable.background_holding)
            setCancelable(false)
            show()
            return this
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode, data)
    }

    private fun delayedStart() {
        if (App.get().passportRepository.passportExists) {
            Single.timer(1500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _ ->
                        atLeastCreated {
                            navigateTo(HomeActivity::class.java)
                            finish()
                        }
                    }
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
            iv_logo.startAnimation(animation)
            tv_note.startAnimation(animation)
            btn_create.visibility = View.VISIBLE
            btn_import.visibility = View.VISIBLE
            btn_create.onClick {
                navigateTo(CreatePassportActivity::class.java)
            }
            btn_import.onClick {
                navigateTo(PassportImportActivity::class.java)
            }
        }
    }
}
