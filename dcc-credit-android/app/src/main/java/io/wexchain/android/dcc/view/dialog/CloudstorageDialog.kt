package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.dialog_help_cloud.*
import kotlinx.android.synthetic.main.dialog_tips_cloud.*

/**
 *Created by liuyang on 2018/8/13.
 */
class CloudstorageDialog(context: Context):Dialog(context) {

    fun createHelpDialog(){
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_help_cloud, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.7).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)
        help_cancel.onClick {
            dismiss()
        }
        show()
    }

    fun createTipsDialog(){
        this.setCancelable(false)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_tips_cloud, null)
        setContentView(view)
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        val d = context.resources.displayMetrics
        lp.width = (d.widthPixels * 0.7).toInt()
        dialogWindow.attributes = lp
        window.setBackgroundDrawableResource(R.drawable.background_holding)
        btn_ok.onClick {
            dismiss()
        }
        show()
    }

}