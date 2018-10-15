package worhavah.regloginlib

import io.wexchain.android.common.getAndroidKeyStoreLoaded
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*

data class AuthKey(
        val keyAlias: String,
        val publicKeyEncoded: ByteArray
    ) {

    fun getPrivateKey(): PrivateKey {
        return (getAndroidKeyStoreLoaded().getEntry(keyAlias, null) as? KeyStore.PrivateKeyEntry)?.privateKey!!
    }

    fun getPrivateKey2(): PrivateKey {
        val k=getAndroidKeyStoreLoaded()
        val k1=k.getEntry(keyAlias, null)
        val k2=k1 as? KeyStore.PrivateKeyEntry
        val k3=k2?.privateKey
        return k3!!
       // return (getAndroidKeyStoreLoaded().getEntry(keyAlias, null) as? KeyStore.PrivateKeyEntry)?.privateKey!!
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthKey

        if (keyAlias != other.keyAlias) return false
        if (!Arrays.equals(publicKeyEncoded, other.publicKeyEncoded)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyAlias.hashCode()
        result = 31 * result + Arrays.hashCode(publicKeyEncoded)
        return result
    }
}