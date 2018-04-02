package io.wexchain.android.common.persist

import android.content.SharedPreferences

/**
 * Created by lulingzhi on 2017/9/28.
 */
class SharedPreferencesKVStore(private val sp: SharedPreferences) : KVStore<String, String> {
    override fun removeAll(collection: Collection<String>) {
        sp.edit().apply {
            collection.forEach { this.remove(it) }
        }.apply()
    }

    override fun getAll(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        sp.all.entries.forEach {
            val v = it.value
            if (v != null && v is String) {
                map[it.key] = v
            }
        }
        return map
    }

    override fun get(key: String): String? = sp.getString(key, null)

    override fun put(key: String, value: String): String? {
        sp.edit().putString(key, value).apply()
        return null
    }

    override fun putAll(map: Map<String, String>) {
        sp.edit().apply {
            map.entries.forEach { (k, v) ->
                this.putString(k, v)
            }
        }.apply()
    }

}