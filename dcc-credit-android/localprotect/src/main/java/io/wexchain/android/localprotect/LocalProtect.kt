package io.wexchain.android.localprotect

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.util.Pair
import io.wexchain.android.common.persist.KVStore
import io.wexchain.android.common.persist.SharedPreferencesKVStore
import io.wexchain.android.common.persist.ValueTransformStoreAgent

/**
 * Created by lulingzhi on 2017/11/22.
 */
object LocalProtect {
    private const val LOG_TAG = "LocalProtect"
    const val PROTECT_SP_NAME = "local_protect"
    const val TYPE = "type"
    const val PARAM = "param"
    const val IRRELEVANT_PARAM = "irrelevant_param_you_should_do_check"

    val currentProtect = MutableLiveData<Pair<LocalProtectType, String?>?>()

    private lateinit var protectStore: KVStore<String,String>

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PROTECT_SP_NAME, Context.MODE_PRIVATE)
        protectStore = SharedPreferencesKVStore(sp)
//        sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key -> reloadProtect() }
        reloadProtect()
    }

    fun init(context: Context,encFunc:(String)->String,decFunc:(String)->String){
        val sp = context.getSharedPreferences(PROTECT_SP_NAME, Context.MODE_PRIVATE)
        protectStore = ValueTransformStoreAgent(SharedPreferencesKVStore(sp),decFunc,encFunc)
//        sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key -> reloadProtect() }
        reloadProtect()
    }

    private fun reloadProtect() {
        currentProtect.value = loadProtect()
    }

    fun setProtect(type: LocalProtectType, param: String) {
        currentProtect.value = Pair(type, param)
        saveProtect(type, param)
//        ld(LOG_TAG, "set new Protect: $type,$param")
    }

    fun disableProtect() {
        currentProtect.value = null
        saveProtect(null)
//        ld(LOG_TAG, "disableProtect.")
    }

    fun loadProtect(): Pair<LocalProtectType, String?>? {
        val store = this.protectStore
        return store.get(TYPE)?.let {
            Pair(LocalProtectType.valueOf(it), store.get(PARAM))
        }
    }

    fun saveProtect(type: LocalProtectType?, param: String? = null) {
        val store = this.protectStore
        if (type == null) {
            store.removeAll(listOf(TYPE, PARAM))
        } else {
            param!!
            store.put(TYPE,type.name)
            store.put(PARAM,param)
        }
    }
}