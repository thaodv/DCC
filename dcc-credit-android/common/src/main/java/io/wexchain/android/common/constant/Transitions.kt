package io.wexchain.android.common.constant

import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.view.View

object Transitions{

    const val CONTENT_DIGITAL_ASSETS = "card_digital_assets"
    const val DIGITAL_ASSETS_LIST = "digital_assets_list"
    const val DIGITAL_ASSETS_AMOUNT_LABEL = "digital_assets_amount_label"
    const val DIGITAL_ASSETS_AMOUNT = "digital_assets_amount"
    const val CONTENT_BACKGROUND = "content_background"
    const val CONTENT_TEXT_DIGITAL_ID = "content_text_digital_id"
    const val STUB_TOP = "stub_top"
    const val STUB_BOTTOM = "stub_bottom"
    const val CARD_PASSPORT = "card_passport"
    const val CARD_PASSPORT_AVATAR = "card_passport_avatar"
    const val CARD_CONTAINER = "card_container"
    const val CARD_BAND = "card_band"
    const val CARD_ALLY = "card_ally"
    const val CARD_CANDY = "card_candy"
    const val CARD_CREDIT = "card_credit"

    @JvmStatic
    fun create(view: View, name: String): Pair<View, String> {
        ViewCompat.setTransitionName(view, name)
        return Pair(view, name)
    }
}