package com.wexmarket.android.network.cookie

import android.content.Context
import java.net.CookieStore
import java.net.HttpCookie

/**
 * Created by lulingzhi on 2017/9/28.
 */
class CookieStoreFactory {
    companion object {
        const val COOKIE_SP_NAME = "cspmn"
        @JvmStatic fun create(context: Context, transform:(String)->HttpCookie, reverseTransform:(HttpCookie)->String):CookieStore {
            val kvStore = SharedPreferencesKVStore(context.getSharedPreferences(COOKIE_SP_NAME, Context.MODE_PRIVATE))
            return KVCookieStore(ValueTransformStoreAgent(kvStore, transform = transform, reverseTransform = reverseTransform))
        }
    }
}