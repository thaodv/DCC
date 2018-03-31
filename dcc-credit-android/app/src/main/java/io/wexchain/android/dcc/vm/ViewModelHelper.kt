package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.wexchain.auth.R

object ViewModelHelper {
    @JvmStatic
    fun Context.getAuthTypeIcon(certificationType: CertificationType?): Drawable? {
        val drawableId = when(certificationType){
            null -> 0
            CertificationType.ID -> R.drawable.shape_id
            CertificationType.PERSONAL -> R.drawable.shape_personal
            CertificationType.BANK -> R.drawable.shape_bank_card
            CertificationType.MOBILE -> R.drawable.shape_mobile
        }
        return ContextCompat.getDrawable(this,drawableId)
    }
}