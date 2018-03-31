package com.wexmarket.android.network.cookie

import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


/**
 * Created by lulingzhi on 2017/9/28.
 */
class KVCookieStore(private val kvStore: KVStore<String, HttpCookie>) : CookieStore {

    /**
     * mem cache of cookies:
     * key = name
     * domain matching & path matching is required for value list
     */
    private val cookies: MutableMap<String, MutableList<HttpCookie>>
            = kvStore.getAll().values
            .groupBy { it.name }
            .mapValues {
                it.value.toMutableList()
            }.toMutableMap()
    private val lock = ReentrantLock()

    private fun nameList(name: String): MutableList<HttpCookie> {
        val list = cookies[name]
        return if (list == null) {
            val newOne = mutableListOf<HttpCookie>()
            cookies[name] = newOne
            newOne
        } else {
            list
        }
    }

    override fun getCookies(): List<HttpCookie> {
        return lock.withLock {
            getAll()
        }
    }

    private fun getAll(): MutableList<HttpCookie> =
            cookies.values.fold(mutableListOf(), { acc, list -> acc.addAll(list);acc })


    override fun add(uri: URI, cookie: HttpCookie?) {
        cookie ?: throw IllegalArgumentException()
        return lock.withLock {
            addInternal(uri, cookie, true)
        }
    }

    private fun addInternal(uri: URI, cookie: HttpCookie, persist: Boolean) {
        val added = addToMemory(cookie)
        if (added && persist) {
            kvStore.put(keyOf(uri, cookie), cookie)
        }
    }

    private fun addToMemory(cookie: HttpCookie): Boolean {
        nameList(cookie.name).add(cookie)
        return true
    }

    private fun keyOf(uri: URI, cookie: HttpCookie): String {
        return "${cookie.domain},${cookie.path},${cookie.name}"
    }


    override fun remove(uri: URI, cookie: HttpCookie): Boolean {
        return lock.withLock {
            val list = nameList(cookie.name)
            list.remove(cookie)
        }
    }

    override fun removeAll(): Boolean {
        var removed = false
        lock.withLock {
            cookies.values.filter { !it.isEmpty() }.forEach {
                it.clear()
                removed = true
            }
        }
        return removed
    }

    override fun get(uri: URI?): MutableList<HttpCookie> {
        uri ?: return mutableListOf()
        lock.withLock {
            return getAll().filter { it.matches(uri) }.toMutableList()
        }
    }

    override fun getURIs(): MutableList<URI> {
        return mutableListOf()//what?? seems a stupid api
    }
}