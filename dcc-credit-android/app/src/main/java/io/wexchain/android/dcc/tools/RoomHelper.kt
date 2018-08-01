package io.wexchain.android.dcc.tools

import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

/**
 * Created by sisel on 2018/1/25.
 */
object RoomHelper {

    val roomExecutor = Executors.newSingleThreadExecutor()

    val roomScheduler = Schedulers.from(roomExecutor)

    fun onRoomIoThread(action: () -> Unit) {
        roomScheduler.scheduleDirect(action)
    }
}