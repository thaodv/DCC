package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.wexchain.auth.R

object ViewModelHelper {
    @JvmStatic
    fun Context.getAuthTypeIcon(authType: AuthType?): Drawable? {
        val drawableId = when(authType){
            null -> 0
            AuthType.ID -> R.drawable.shape_id
            AuthType.PERSONAL -> R.drawable.shape_personal
            AuthType.BANK -> R.drawable.shape_bank_card
            AuthType.MOBILE -> R.drawable.shape_mobile
        }
        return ContextCompat.getDrawable(this,drawableId)
    }
}