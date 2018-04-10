package io.wexchain.android.dcc.tools

import io.reactivex.functions.BiFunction


fun <T1, T2> pair(): BiFunction<T1, T2, Pair<T1, T2>> {
    return BiFunction { t1, t2 -> Pair(t1, t2) }
}