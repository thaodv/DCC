package io.wexchain.android.common

import android.app.ActivityManager
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Process
import android.support.annotation.AttrRes
import android.support.annotation.RequiresApi
import android.support.annotation.RequiresPermission
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.TypedValue
import android.webkit.WebView

/**
 * Created by lulingzhi on 2017/10/25.
 */

fun Context.getCurrentProcessName(): String? {
    val pid = Process.myPid()
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return am.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
}

fun Context.dp2px(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.getClipboardManager(): ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

@RequiresApi(Build.VERSION_CODES.M)
fun Context.getFingerPrintManager(): FingerprintManager {
    return this.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
}

fun Context.getAttrDrawable(@AttrRes attr: Int): Drawable {
    return this.obtainStyledAttributes(kotlin.intArrayOf(attr))
            .run {
                val drawable = getDrawable(0)
                recycle()
                drawable
            }
}

fun Context.getAttrId(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

/**
 * check fingerprint is supported by os and hardware
 */
@RequiresApi(Build.VERSION_CODES.M)
@RequiresPermission(android.Manifest.permission.USE_FINGERPRINT)
fun Context.fingerPrintAvailable(): Boolean {
    return FingerprintManagerCompat.from(this).isHardwareDetected
}

@RequiresApi(Build.VERSION_CODES.M)
@RequiresPermission(android.Manifest.permission.USE_FINGERPRINT)
fun Context.fingerPrintEnrolled(): Boolean {
    return FingerprintManagerCompat.from(this).hasEnrolledFingerprints()
}

fun Context.setWebViewDebuggable(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}