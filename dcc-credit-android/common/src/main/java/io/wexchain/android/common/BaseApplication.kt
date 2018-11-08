package io.wexchain.android.common

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import kotlin.properties.Delegates

/**
 *Created by liuyang on 2018/7/27.
 */
open class BaseApplication : Application() {

    val isLogin = MutableLiveData<Boolean>()
            .apply {
                postValue(false)
            }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var context by Delegates.notNull<BaseApplication>()
    }
}