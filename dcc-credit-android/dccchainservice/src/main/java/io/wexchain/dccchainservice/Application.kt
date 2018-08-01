package io.wexchain.dccchainservice

import android.annotation.SuppressLint
import android.content.Context

/**
 *Created by liuyang on 2018/7/25.
 */
@SuppressLint("StaticFieldLeak")
object Application {

    private lateinit var CONTEXT: Context

    fun getContext(): Context {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val method = activityThreadClass.getMethod("currentApplication")
        CONTEXT = method.invoke(null) as Context
        return CONTEXT
    }
}