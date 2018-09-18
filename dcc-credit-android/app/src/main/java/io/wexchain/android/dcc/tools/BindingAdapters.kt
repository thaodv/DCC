package io.wexchain.android.dcc.tools

import android.databinding.BindingAdapter
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.wexmarket.android.barcode.util.QrCodeDrawable
import io.wexchain.android.dcc.network.GlideApp
import io.wexchain.android.dcc.view.SwitchButton
import io.wexchain.android.dcc.view.state.ExceedAware

@set:BindingAdapter("visibleOrGone")
var View.visibleOrGone
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }

@set:BindingAdapter("SwitchStatus")
var SwitchButton.switchStatus
    get() = this.isChecked
    set(value) {
        this.isChecked = value
    }



@set:BindingAdapter("visibleOrInvisible")
var View.visibleOrInvisible
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

@BindingAdapter("imageRes")
fun ImageView.setImageRes(
        @DrawableRes
        res: Int?
) {
    if (res == null || res == 0) {
        this.setImageDrawable(null)
    } else {
        this.setImageResource(res)
    }
}

@BindingAdapter("imageRawBytes", "errorRes")
fun ImageView.setImageRawBytes(imageRawBytes: ByteArray?, errorRes: Drawable?) {
    if (imageRawBytes != null) {
        try {

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false

            val decoded = BitmapFactory.decodeByteArray(imageRawBytes, 0, imageRawBytes.size)
            this.setImageBitmap(decoded)
        } catch (e: IllegalArgumentException) {
            this.setImageDrawable(errorRes)
        }
    } else {
        this.setImageDrawable(errorRes)
    }
}

@BindingAdapter(value = ["imageUrl", "errorRes"], requireAll = false)
fun setImageUrl(imageView: ImageView, url: String?, errorRes: Drawable?) {
    GlideApp.with(imageView).load(url)
            .error(errorRes)
            .into(imageView)
}

@BindingAdapter("dataList")
fun <T> RecyclerView.setDataList(dataList: List<T>?) {
    (this.adapter as? ListAdapter<T, *>)?.submitList(dataList)
}

@BindingAdapter("bgUrl")
fun View.setBackgroundUrl(url: String?) {
    val target = ViewBackgroundTarget(this)
    if (url.isNullOrBlank()) {
        GlideApp.with(this)
                .clear(target)
    } else {
        GlideApp.with(this)
                .load(url)
                .into(target)
    }
}

@BindingAdapter("imageUri", "errorRes")
fun setImageUri(imageView: ImageView, uri: Uri?, errorRes: Drawable?) {
    if (uri == null || uri.toString().isBlank()) {
        imageView.setImageDrawable(errorRes)
    } else {
        imageView.setImageURI(uri)
    }
}

@BindingAdapter("qrContent")
fun setQrContentSrc(imageView: ImageView, qrContent: String?) {
    qrContent ?: return
    val drawable = imageView.drawable
    if (drawable != null && drawable is QrCodeDrawable) {
        drawable.setQrContent(qrContent)
    } else {
        imageView.setImageDrawable(QrCodeDrawable().apply {
            this.setQrContent(qrContent)
        })
    }
}

@BindingAdapter("passwordSecure")
fun setPasswordSecure(editText: EditText, passwordSecure: Boolean) {
    if (passwordSecure) {
        editText.transformationMethod = PasswordTransformationMethod()
    } else {
        editText.transformationMethod = DoNothingTransformationMethod()
    }
    editText.setSelection(editText.text.length)
}

@BindingAdapter("state_exceeded")
fun View.setExceeded(exceeded: Boolean) {
    if (this is ExceedAware)
        this.isExceeded = exceeded
}
