package io.wexchain.android.common

import android.os.Looper
import android.webkit.WebView
import java.util.*


fun isOnMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

/**
 * return true is English
 */
fun checkLanguage(): Boolean {
    val country = Locale.getDefault().country
    return (country == "US" || country == "UK")
}

fun WebView.loadLanguageUrl(url: String) {
    if (checkLanguage()) {
        var languageurl = ""
        val list = url.split('.').toList()
        for ((i, s) in list.withIndex()) {
            languageurl += when (i) {
                list.size - 1 -> s
                list.size - 2 -> s + "_en."
                else -> "$s."
            }
        }
        this.loadUrl(languageurl)
    } else {
        this.loadUrl(url)
    }

}
