package io.wexchain.android.common

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import java.lang.ref.WeakReference


/**
 * Created by lulingzhi on 2017/11/15.
 */

object Pop {

    private var prevToast: WeakReference<Toast>? = null

    fun toast(text: CharSequence, context: Context) {
        toastInternal {
            buildAndShowToast(context, text, Toast.LENGTH_SHORT)
        }
    }

    fun toast(@StringRes stringId: Int, context: Context) {
        toastInternal {
            buildAndShowToast(context, context.getText(stringId), Toast.LENGTH_SHORT)
        }
    }

    fun longToast(text: CharSequence, context: Context) {
        toastInternal {
            buildAndShowToast(context, text, Toast.LENGTH_LONG)
        }
    }

    private fun buildAndShowToast(context: Context, text: CharSequence, length: Int): Toast {
        return buildToast(context, text, length)
                .apply {
                    //todo customize
                    show()
                }
    }

    @SuppressLint("ShowToast")
    private fun buildToast(context: Context, text: CharSequence, duration: Int): Toast {
        val attrId = context.getAttrId(R.attr.customToastLayout)
        return if (attrId > 0){
            val view = LayoutInflater.from(context).inflate(attrId, null)
            val toast = Toast(context)
            toast.duration = duration
            toast.view = view
            view.findViewById<TextView>(android.R.id.message).text = text
            toast
        }else {
            Toast.makeText(context, text, duration)
        }
    }

    fun cancelToast() {
        val p = prevToast?.get()
        if (p != null && p.view.windowToken != null) {
            p.cancel()
        }
    }

    private fun toastInternal(makeToast: () -> Toast) {
        if (isOnMainThread()) {
            cancelToast()
            val toast = makeToast()
            prevToast = WeakReference(toast)
        } else {
            runOnMainThread {
                cancelToast()
                val toast = makeToast()
                prevToast = WeakReference(toast)
            }
        }
    }

}