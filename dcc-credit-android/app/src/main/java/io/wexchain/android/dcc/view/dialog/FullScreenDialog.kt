package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.StyleRes
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import io.wexchain.android.dcc.tools.setQrContentSrc
import io.wexchain.dcc.R


/**
 * Created by lulingzhi on 2017/11/23.
 */
class FullScreenDialog(context: Context, @StyleRes theme: Int = R.style.FullWidthDialog) : Dialog(context, theme) {
    companion object {
        fun createQr(context: Context, content: String, autoDismiss: Boolean = true): FullScreenDialog {
            val dialog = FullScreenDialog(context)
            dialog.setContentView(R.layout.dialog_fullscreen_image)
            val imageView = dialog.findViewById<ImageView>(R.id.iv_fullscreen)
            if (autoDismiss) {
                imageView.setOnClickListener {
                    dialog.dismiss()
                }
            }
            setQrContentSrc(imageView, content)
            dialog.makeWindowFullscreenAndTransparent()
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            return dialog
        }

        fun createLoading(context: Context): FullScreenDialog {
            val dialog = FullScreenDialog(context)
            dialog.setContentView(R.layout.dialog_fullscreen_loading)
            val ivg = dialog.findViewById<ImageView>(R.id.iv_gear)
            val anim = AnimationUtils.loadAnimation(context, R.anim.rotate)
            ivg.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    v?.clearAnimation()
                }

                override fun onViewAttachedToWindow(v: View?) {
                    v?.startAnimation(anim)
                }
            })
            dialog.makeWindowFullscreenAndTransparent()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            return dialog
        }

        fun Dialog.makeWindowFullscreenAndTransparent(onShowExtra:(()->Unit)?=null){
            setOnShowListener {
                val window = this.window
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = Color.TRANSPARENT
                    window.navigationBarColor = Color.TRANSPARENT
                }
                onShowExtra?.invoke()
            }
        }
    }
}