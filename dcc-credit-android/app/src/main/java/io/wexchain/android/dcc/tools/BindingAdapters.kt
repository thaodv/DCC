package io.wexchain.android.dcc.tools

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import com.wexmarket.android.barcode.util.QrCodeDrawable
import io.wexchain.android.dcc.network.GlideApp

@BindingAdapter("imageRawBytes","errorRes")
fun ImageView.setImageRawBytes(imageRawBytes: ByteArray?,errorRes:Drawable?){
    if(imageRawBytes!=null) {
        try {
            val decoded = android.graphics.BitmapFactory.decodeByteArray(imageRawBytes, 0, imageRawBytes.size)
            this.setImageBitmap(decoded)
        }catch (e:IllegalArgumentException){
            this.setImageDrawable(errorRes)
        }
    }else{
        this.setImageDrawable(errorRes)
    }
}

@BindingAdapter("imageUrl", "errorRes")
fun setImageUrl(imageView: ImageView, url: String?, errorRes: Drawable?) {
    GlideApp.with(imageView).load(url)
            .error(errorRes)
            .into(imageView)
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
