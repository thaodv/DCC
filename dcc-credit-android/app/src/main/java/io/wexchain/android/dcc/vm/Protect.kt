package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.UseProtect

class Protect(application: Application) : AndroidViewModel(application), UseProtect {

    override val type = ObservableField<LocalProtectType>()
    override val protectChallengeEvent = SingleLiveEvent<(Boolean) -> Unit>()

    val protectEnableEvent = SingleLiveEvent<Void>()
    val protectDisabledEvent = SingleLiveEvent<Void>()

    fun sync(lifecycleOwner: LifecycleOwner) {
        LocalProtect.currentProtect.observe(lifecycleOwner, Observer {
            type.set(it?.first)
        })
    }

    fun switchProtectEnable() {
        val enabled = type.get() != null
        if (enabled) {
            verifyProtect {
                LocalProtect.disableProtect()
                protectDisabledEvent.call()
            }
        } else {
            protectEnableEvent.call()
        }
    }
}