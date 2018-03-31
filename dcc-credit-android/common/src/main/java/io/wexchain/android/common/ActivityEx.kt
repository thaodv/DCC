package io.wexchain.android.common

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * Created by sisel on 2018/3/27.
 */

inline fun <T : Activity> Activity.navigateTo(activity: Class<T>, crossinline extras: Intent.() -> Unit = {}) {
    this.startActivity(Intent(this, activity).apply {
        this.extras()
    })
}


fun AppCompatActivity.toast(text: CharSequence, context: Context = this) {
    atLeastCreated {
        Pop.toast(text, context)
    }
}

fun AppCompatActivity.toast(@StringRes stringId: Int, context: Context = this) {
    atLeastCreated {
        Pop.toast(stringId, context)
    }
}

fun Activity.setWindowExtended(){
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(key: String? = null): T {
    val vmClass = T::class.java
    return if (key == null) {
        ViewModelProviders.of(this).get(vmClass)
    } else {
        ViewModelProviders.of(this).get(key, vmClass)
    }
}

fun Activity.transitionBundle(vararg pairs: Pair<View, String>): Bundle? {
    return withTransitionEnabled {
        ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs).toBundle()
    }
}

inline fun <T> withTransitionEnabled(block: () -> T): T? {
    return if (BuildConfig.DEBUG) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            block()
        } else null
    } else {
        // up to N to avoid memory leak
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            block()
        } else null
    }
}