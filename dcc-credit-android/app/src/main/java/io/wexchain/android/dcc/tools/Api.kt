package io.wexchain.android.dcc.tools

import io.wexchain.android.dcc.App

/**
 *Created by liuyang on 2018/7/20.
 */

val AppContext = App.get()

val chainFrontEndApi = AppContext.chainFrontEndApi

val scfApi = AppContext.scfApi

val marketingApi = AppContext.marketingApi

fun getString(id: Int): String {
    return AppContext.resources.getString(id)
}