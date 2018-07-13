package io.wexchain.android.common

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import java.io.File

/**
 * Created by sisel on 2018/3/27.
 */

inline fun <T : Activity> Activity.navigateTo(activity: Class<T>,options:Bundle?=null, crossinline extras: Intent.() -> Unit = {}) {
    this.startActivity(Intent(this, activity).apply {
        this.extras()
    },options)
}

fun Activity.resultOk(data:(Intent.()->Unit)? = null){
    val resultData = data?.run { Intent().apply { data() } }
    this.setResult(Activity.RESULT_OK,resultData)
    finish()
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


@SuppressLint("CommitTransaction")
fun FragmentActivity.replaceFragment(
        fragment: Fragment,
        @IdRes containerViewId: Int = android.R.id.content,
        tag: String? = null,
        backStackStateName: String? = null
) {
    supportFragmentManager.commitTransaction {
        replace(containerViewId, fragment, tag)
        backStackStateName?.let { addToBackStack(it) }
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

fun Context.getVersionName(): String {
    var versionName = "1.0.0"
    try {
        versionName = this.packageManager.getPackageInfo(this.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

 fun Context.installApk(file: File) {
    val data: Uri
    val intent = Intent(Intent.ACTION_VIEW)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        data = FileProvider.getUriForFile(this, "io.wexchain.dcc.fileprovider", file)
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } else {
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}