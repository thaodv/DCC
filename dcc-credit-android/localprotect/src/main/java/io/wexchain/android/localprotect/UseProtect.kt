package io.wexchain.android.localprotect

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 * Created by lulingzhi on 2017/12/4.
 */
interface UseProtect {
    val type: ObservableField<LocalProtectType>
    val protectChallengeEvent: SingleLiveEvent<(Boolean) -> Unit>

    fun syncProtect(lifecycleOwner: LifecycleOwner) {
        LocalProtect.currentProtect.observe(lifecycleOwner, Observer {
            type.set(it?.first)
        })
    }

    fun verifyProtect(fail: () -> Unit = {}, next: () -> Unit) {
        protectChallengeEvent.value = {
            if (it) {
                next()
            } else {
                fail()
            }
        }
    }

}
