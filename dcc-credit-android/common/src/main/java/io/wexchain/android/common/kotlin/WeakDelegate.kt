package io.wexchain.android.common.kotlin

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakDelegate<R, T> : ReadWriteProperty<R, T?> {

    private var _value: WeakReference<T>? = null

    override fun getValue(thisRef: R, property: KProperty<*>): T? {
        return _value?.get()
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T?) {
        this._value = if (value == null) {
            null
        } else {
            WeakReference(value)
        }
    }
}

public fun <R, T> weak(): WeakDelegate<R, T> = WeakDelegate()