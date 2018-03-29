package io.wexchain.android.dcc.tools

import android.app.Activity
import android.content.Intent
import android.support.v7.app.ActionBar

/**
 * Created by sisel on 2018/3/27.
 */

inline fun <T : Activity> Activity.navigateTo(activity: Class<T>, crossinline extras: Intent.() -> Unit = {}) {
    this.startActivity(Intent(this, activity).apply {
        this.extras()
    })
}
