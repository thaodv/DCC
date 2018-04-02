package io.wexchain.android.dcc.tools

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
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