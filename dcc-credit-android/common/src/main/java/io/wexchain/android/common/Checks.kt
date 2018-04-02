package io.wexchain.android.common

import android.os.Looper


fun isOnMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()