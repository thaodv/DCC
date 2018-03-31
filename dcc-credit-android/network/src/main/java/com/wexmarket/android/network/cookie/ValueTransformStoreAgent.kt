package com.wexmarket.android.network.cookie

/**
 * Created by lulingzhi on 2017/9/29.
 */
class ValueTransformStoreAgent<K, VS, VT>(
        private val originStore: KVStore<K, VS>,
        private val transform: (VS) -> VT,
        private val reverseTransform: (VT) -> VS
) : KVStore<K, VT> {
    override fun removeAll(collection: Collection<K>) {
        originStore.removeAll(collection)
    }

    override fun get(key: K): VT? {
        return originStore.get(key)?.let(transform)
    }

    override fun put(key: K, value: VT): VT? {
        return originStore.put(key, reverseTransform.invoke(value))?.let(transform)
    }

    override fun putAll(map: Map<K, VT>) {
        originStore.putAll(map.mapValues { reverseTransform.invoke(it.value) })
    }

    override fun getAll(): Map<K, VT> {
        return originStore.getAll().mapValues { transform.invoke(it.value) }
    }

}