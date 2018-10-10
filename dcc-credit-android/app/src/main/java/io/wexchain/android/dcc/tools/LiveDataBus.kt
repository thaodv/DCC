package io.wexchain.android.dcc.tools

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.NonNull
import io.wexchain.android.dcc.tools.LiveDataBus.ObserverWrapper
import java.util.Map


/**
 *Created by liuyang on 2018/7/31.
 */
class LiveDataBus {

    private var bus: MutableMap<String, BusMutableLiveData<Any>> = mutableMapOf()

    private object SingletonHolder {
        val DEFAULT_BUS = LiveDataBus()
    }

    fun get(): LiveDataBus {
        return SingletonHolder.DEFAULT_BUS
    }

    fun <T> with(key: String, type: Class<T>): MutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as MutableLiveData<T>
    }

    fun with(key: String): MutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>?) : Observer<T> {

        override fun onChanged(t: T?) {
            observer.let {
                if (!isCallOnObserve) {
                    it?.onChanged(t)
                }
            }

        }

        private val isCallOnObserve: Boolean
            get() {
                val stackTrace = Thread.currentThread().stackTrace
                if (stackTrace != null && stackTrace.isNotEmpty()) {
                    for (element in stackTrace) {
                        if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                            return true
                        }
                    }
                }
                return false
            }

    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {

        private val observerMap = mutableMapOf<Observer<T>, Observer<T>>()

        override fun observe(@NonNull owner: LifecycleOwner, @NonNull observer: Observer<T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun observeForever(@NonNull observer: Observer<T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = ObserverWrapper(observer)
            }
            super.observeForever(observerMap[observer]!!)
        }

        override fun removeObserver(@NonNull observer: Observer<T>) {
            val realObserver: Observer<T>? = if (observerMap.containsKey(observer)) {
                observerMap.remove(observer)
            } else {
                observer
            }
            super.removeObserver(realObserver!!)
        }

        @Throws(Exception::class)
        private fun hook(@NonNull observer: Observer<T>) {
            //get wrapper's version
            val classLiveData = LiveData::class.java
            val fieldObservers = classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers.get(this)
            val classObservers = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod("get", Object::class.java)
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = (objectWrapperEntry).value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be null!")
            }
            val classObserverWrapper = objectWrapper.javaClass.superclass
            val fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion.get(this)
            fieldLastVersion.set(objectWrapper, objectVersion)
        }
    }
}