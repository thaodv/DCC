package io.wexchain.android.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.support.v4.util.ObjectsCompat

/**
 * Created by sisel on 2018/1/31.
 */
fun <A, B, R> zipLiveData(
        a: LiveData<A>, b: LiveData<B>,
        zipper: (A, B) -> R
): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = zipper(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

fun <T, R> LiveData<T>.map(mapFunc: (T?) -> R): LiveData<R> {
    return Transformations.map(this, mapFunc)
}

fun <T, R> LiveData<T>.switchMap(mapFunc: (T?) -> LiveData<R>): LiveData<R> {
    return Transformations.switchMap(this, mapFunc)
}

fun <T> LiveData<T>.distinct(eqFunc: (T?, T?) -> Boolean = ObjectsCompat::equals): LiveData<T> {
    return MediatorLiveData<T>().apply {
        var lastValue: T? = null
        addSource(this@distinct) {
            if (!eqFunc(it, lastValue)) {
                lastValue = it
                this.value = it
            }
        }
    }
}

fun <T> LiveData<List<T>>.filter(filterFunc: (T) -> Boolean): LiveData<List<T>> {

    return MediatorLiveData<List<T>>().apply {
        var lastValue: List<T>? = null
        addSource(this@filter) {
            val filtered = it?.filter(filterFunc)
            if (lastValue != filtered) {
                lastValue = filtered
                this.value = filtered
            }
        }
    }
}

fun <T> LiveData<T>.observing(
        doOnChange: (T?) -> Unit = {},
        doOnActive: (T?) -> Unit = {},
        doOnInactive: (T?) -> Unit = {}
): LiveData<T> {
    return object : MediatorLiveData<T>() {
        init {
            addSource(this@observing) {
                this.value = it
                doOnChange(it)
            }
        }

        override fun onActive() {
            super.onActive()
            doOnActive(this.value)
        }

        override fun onInactive() {
            super.onInactive()
            doOnInactive(this.value)
        }
    }
}