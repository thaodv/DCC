package io.wexchain.android.dcc.repo

import android.content.Context
import io.wexchain.android.common.persist.SharedPreferencesKVStore
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ScfTokenManager(context: Context){
    private val tokenStore = SharedPreferencesKVStore(context.getSharedPreferences(TOKEN_SP_NAME,Context.MODE_PRIVATE))
    private val lock = ReentrantReadWriteLock()

    var scfToken:String?
        get() {
            lock.read {
                return tokenStore.get(SCF_TOKEN)
            }
        }
        set(value) {
            lock.write {
                if(value!=null){
                    tokenStore.put(SCF_TOKEN,value)
                }else{
                    tokenStore.removeAll(listOf(SCF_TOKEN))
                }
            }
        }

    companion object {
        private const val TOKEN_SP_NAME = "scf_token_store"
        private const val SCF_TOKEN = "scf_token"
    }
}