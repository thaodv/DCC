package io.wexchain.ipfs.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *Created by liuyang on 2018/8/3.
 */

fun <T> Single<T>.doMain(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.doBack(): Single<T> {
    return this.observeOn(Schedulers.io())
}

fun <T> Observable<T>.doMain(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.doBack(): Observable<T> {
    return this.observeOn(Schedulers.io())
}