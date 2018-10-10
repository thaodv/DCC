package io.wexchain.android.dcc.tools

import io.wexchain.android.common.runOnMainThread
import org.jetbrains.anko.doAsync

/**
 *Created by liuyang on 2018/10/10.
 */


/**
 * 数据库操作
 */
fun onRoomIoThread(action: () -> Unit) {
    RoomHelper.onRoomIoThread {
        action()
    }
}

/**
 * 子线程
 */
fun <T> T.onWorkThread(action: () -> Unit) {
    doAsync {
        action()
    }
}

/**
 * 主线程
 */
fun onUiThread(action: () -> Unit) {
    runOnMainThread {
        action()
    }
}