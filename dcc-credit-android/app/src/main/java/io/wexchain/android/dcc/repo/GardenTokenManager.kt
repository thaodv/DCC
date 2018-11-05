package io.wexchain.android.dcc.repo

import android.content.Context
import android.support.annotation.GuardedBy
import io.wexchain.android.common.persist.SharedPreferencesKVStore
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class GardenTokenManager(context: Context) {
    private val tokenStore = SharedPreferencesKVStore(context.getSharedPreferences(TOKEN_SP_NAME, Context.MODE_PRIVATE))
    private val lock = ReentrantReadWriteLock()

    @get:GuardedBy("lock")
    @set:GuardedBy("lock")
    var gardenToken: String?
        get() {
            lock.read {
                return tokenStore.get(GARDEN_TOKEN)
            }
        }
        set(value) {
            lock.write {
                if (value != null) {
                    tokenStore.put(GARDEN_TOKEN, value)
                } else {
                    tokenStore.removeAll(listOf(GARDEN_TOKEN))
                }
            }
        }

    companion object {
        private const val TOKEN_SP_NAME = "garden_token_store"
        private const val GARDEN_TOKEN = "garden_token"
    }
}
