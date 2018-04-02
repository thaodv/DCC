package io.wexchain.dccchainservice.util

import java.security.PrivateKey
import java.security.Signature

object ParamSignatureUtil{
    /**
     * sha256/rsa2048
     */
    const val SIGN_ALGORITHM = "SHA256withRSA"
    @JvmStatic
    fun sign(privateKey: PrivateKey,params:Map<String,String>): String {
        val bytes = params.entries.sortedBy { it.key }.joinToString(separator = "&") { "${it.key}=${it.value}" }.toByteArray(Charsets.UTF_8)
        return privateKey.sign(bytes).toHex()
    }

    private fun PrivateKey.sign(data: ByteArray): ByteArray {
        require(this.algorithm.toUpperCase() == "RSA")
        val signature = Signature.getInstance(SIGN_ALGORITHM)
        signature.initSign(this)
        signature.update(data)
        return signature.sign()
    }


}