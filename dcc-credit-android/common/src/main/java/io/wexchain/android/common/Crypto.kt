package io.wexchain.android.common

import java.security.KeyStore

const val ANDROID_KEY_STORE = "AndroidKeyStore"

fun getAndroidKeyStoreLoaded(): KeyStore {
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    keyStore.load(null)
    return keyStore
}