package io.wexchain.android.dcc.domain

import java.util.*

data class AuthKey(
        val keyAlias:String,
        val publicKeyEncoded:ByteArray
) {
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