package io.wexchain.android.dcc.vm.domain

import io.wexchain.android.idverify.IdCardEssentialData
import java.util.*

data class IdCardCertData(
    val essentialData: IdCardEssentialData,
    val photo:ByteArray?,
    val idFront:ByteArray,
    val idBack:ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdCardCertData

        if (essentialData != other.essentialData) return false
        if (!Arrays.equals(photo, other.photo)) return false
        if (!Arrays.equals(idFront, other.idFront)) return false
        if (!Arrays.equals(idBack, other.idBack)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = essentialData.hashCode()
        result = 31 * result + Arrays.hashCode(photo)
        result = 31 * result + Arrays.hashCode(idFront)
        result = 31 * result + Arrays.hashCode(idBack)
        return result
    }
}