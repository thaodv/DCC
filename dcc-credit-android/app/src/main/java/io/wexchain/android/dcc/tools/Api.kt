package io.wexchain.android.dcc.tools

import io.wexchain.android.dcc.App
import io.wexchain.ipfs.utils.AES256

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

fun Log(tag: String, message: CharSequence) {
    LogUtils.e(tag, message.toString())
}

fun ByteArray.toHex(): String {
    return AES256.bytes2Hex(this)
}

fun ByteArray.toHash256(): ByteArray {
    return AES256.tohash256Deal(this)
}