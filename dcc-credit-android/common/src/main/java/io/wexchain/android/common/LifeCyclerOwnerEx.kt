package io.wexchain.android.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.MainThread
import android.util.Log

/**
 * Created by sisel on 2018/3/5.
 */
inline fun LifecycleOwner.atLeastResumed(
    @MainThread
    action: () -> Unit
) {
    atLeastOnState(Lifecycle.State.RESUMED, action)
}

inline fun LifecycleOwner.atLeastStarted(
    @MainThread
    action: () -> Unit
) {
    atLeastOnState(Lifecycle.State.STARTED, action)
}

inline fun LifecycleOwner.atLeastCreated(
    @MainThread
    action: () -> Unit
) {
    atLeastOnState(Lifecycle.State.CREATED, action)
}

inline fun LifecycleOwner.atLeastOnState(
    state: Lifecycle.State,
    action: () -> Unit
) {
    if (this.lifecycle.currentState.isAtLeast(state)) {
        action()
    }else{
        Lg.withLogEnabled {
            Log.w("lifecycle","lifecycle not satisfied for action")
        }
    }
}

