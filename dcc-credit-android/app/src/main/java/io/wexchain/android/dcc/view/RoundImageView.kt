package io.wexchain.android.dcc.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * Created by sisel on 2018/1/31.
 */
class RoundImageView : AppCompatImageView {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)


    override fun setImageDrawable(drawable: Drawable?) {
        val r = if (drawable == null) null else RoundDrawableWrapper(drawable)
        super.setImageDrawable(r)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        replaceDrawable()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        replaceDrawable()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        replaceDrawable()
    }

    private fun replaceDrawable() {
        val r = if (drawable == null) null else RoundDrawableWrapper(drawable)
        super.setImageDrawable(r)
        invalidate()
    }
}