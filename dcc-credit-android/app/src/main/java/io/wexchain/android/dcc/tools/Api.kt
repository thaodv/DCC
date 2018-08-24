package io.wexchain.android.dcc.tools

import com.google.gson.Gson
import io.wexchain.android.dcc.App
import io.wexchain.ipfs.utils.AES256
import java.io.File

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

fun ByteArray.toHex(): String {
    return AES256.bytes2Hex(this)
}

fun ByteArray.toHash256(): ByteArray {
    return AES256.tohash256Deal(this)
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
            val tmp = tmps.substring(3, tmps.length )
            tmp
        } else {
            s
        }
    }
    return size
}