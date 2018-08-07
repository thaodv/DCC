package io.wexchain.ipfs

import android.annotation.SuppressLint
import android.content.Context

/**
 *Created by liuyang on 2018/7/25.
 */
@SuppressLint("StaticFieldLeak")
object Application {

    private var CONTEXT: Context? = null

    fun getContext(): Context {
        CONTEXT?.let {
            return it
        }
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val method = activityThreadClass.getMethod("currentApplication")
        CONTEXT = method.invoke(null) as Context
        return CONTEXT!!
    }
}