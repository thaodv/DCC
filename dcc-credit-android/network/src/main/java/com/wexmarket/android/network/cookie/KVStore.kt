package com.wexmarket.android.network.cookie

/**
 * Created by lulingzhi on 2017/9/28.
 */
interface KVStore<K,V> {
    fun get(key:K):V?
    fun put(key: K,value:V):V?
    fun putAll(map:Map<K,V>)
    fun getAll():Map<K,V>
    fun removeAll(collection: Collection<K>)
}