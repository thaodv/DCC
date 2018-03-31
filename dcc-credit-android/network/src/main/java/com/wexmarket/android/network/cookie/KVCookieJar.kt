package com.wexmarket.android.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by lulingzhi on 2017/9/30.
 */
class KVCookieJar(
        private val kvStore: KVStore<String, Cookie>
) : CookieJar {

    private val list = kvStore.getAll().values.toMutableList()
    private val lock = ReentrantLock()

    override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
        cookies?.let { c ->
            val persistCookies = c.filter { it.persistent() }
            if (persistCookies.isEmpty()) return
            lock.withLock {
                list.addAll(c)
                kvStore.putAll(c.associateBy { it.persistKey })
            }
        }
    }

    override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
        url ?: return mutableListOf()
        return lock.withLock {
            val matchList = list.filter { it.matches(url) }.toMutableList()
            val expired = matchList.filter { it.isExpired }
            list.removeAll(expired)
            kvStore.removeAll(expired.map { it.persistKey })
            matchList.removeAll(expired)
            matchList
        }
    }

    private val Cookie.persistKey:String
        get() = "${this.domain()},${path()},${this.name()}"

    private val Cookie.isExpired
        get() = this.expiresAt() < System.currentTimeMillis()
}