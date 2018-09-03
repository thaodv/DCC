package io.wexchain.android.dcc.tools

import android.app.ActivityManager
import android.content.Context
import android.os.Looper
import com.google.gson.Gson
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.experimental.and

/**
 *Created by liuyang on 2018/7/20.
 */

fun getString(id: Int): String {
    return App.get().resources.getString(id)
}

fun <T : Any> T.Log(message: CharSequence?) {
    val clazz = (this as java.lang.Object).`class` as Class<T>
    LogUtils.e(clazz.simpleName, message?.toString())
}

/*fun Log(tag: String, message: CharSequence) {
    LogUtils.e(tag, message.toString())
}*/

fun ByteArray.toSha256(): ByteArray {
    return MessageDigest.getInstance("SHA256").digest(this)
}


fun File.reName(newName: String) {
    FileUtils.rename(this, newName)
}

fun Any.toJson(): String {
    return Gson().toJson(this)
}

fun <T> String.toBean(classOf: Class<T>): T {
    return Gson().fromJson<T>(this, classOf)
}

fun File.formatSize(): String {
    val memorySize = FileUtils.byte2FitMemorySize(this.length())
    val split = memorySize.split('.')
    var size = ""
    for ((index, s) in split.withIndex()) {
        size += if (index == split.size - 1) {
            val tmps = split[index]
            val tmp = tmps.substring(3, tmps.length)
            tmp
        } else {
            s
        }
    }
    return size
}

/**
 * 转16进制数
 * @param withPrefix true 带0x 默认不带
 */
fun ByteArray.toHexString(offset: Int, length: Int, withPrefix: Boolean): String {
    val stringBuilder = StringBuilder()
    if (withPrefix) {
        stringBuilder.append("0x")
    }

    for (i in offset until offset + length) {
        stringBuilder.append(String.format("%01x", this[i] and 255.toByte()))
    }

    return stringBuilder.toString()
}

fun ByteArray.toHexString(withPrefix: Boolean = false): String {
    return this.toHexString(0, this.size, withPrefix)
}

fun BigInteger.toHexString(withPrefix: Boolean = false): String {
    val toByteArray = this.toByteArray()
    return toByteArray.toHexString(0, toByteArray.size, withPrefix)
}

fun String.fixLengthBins(): String {
    val bins = StringBuilder(this)
    val len = bins.length
    for (i in 0 until 64 - len) {
        bins.insert(0, '0')
    }
    return bins.toString()
}

fun Passport.getPrivateKey(): String {
    return Numeric.toHexStringNoPrefix(this.credential.ecKeyPair.privateKey).fixLengthBins()
}

fun Context.getProcessName(): String? {
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningApps = am.runningAppProcesses ?: return null
    return runningApps
            .firstOrNull { it.pid == android.os.Process.myPid() && it.processName != null }
            ?.processName
}

fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()
