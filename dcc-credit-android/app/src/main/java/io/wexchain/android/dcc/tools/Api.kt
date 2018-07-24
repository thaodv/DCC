package io.wexchain.android.dcc.tools

import io.wexchain.android.dcc.App

/**
 *Created by liuyang on 2018/7/20.
 */

val appContext = App.get()

fun getString(id: Int): String {
    return appContext.resources.getString(id)
}

fun <T : Any> T.log(message: CharSequence) {
    val clazz = (this as java.lang.Object).`class` as Class<T>
    LogUtils.e(clazz.simpleName, message.toString())
}

fun log(tag: String, message: CharSequence) {
    LogUtils.e(tag, message.toString())
}