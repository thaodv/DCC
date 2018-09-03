package io.wexchain.ipfs.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES 数据加密解密
 * Created by liuyang on 2018/8/6.
 */
object AES256 {

    private const val KEY_ALGORITHM = "AES"
    private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val ivParameter = "1234567890123456"

    /**
     * AES256  加密
     */
    fun encrypt(sSrc: String, key: String, ivm: String = ivParameter): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val raw = key.get32MD5().toByteArray()
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val iv = IvParameterSpec(ivm.get16Md5().toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(sSrc.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun encrypt(data: ByteArray, key: String, ivm: String = ivParameter): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val raw = key.get32MD5().toByteArray()
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val iv = IvParameterSpec(ivm.get16Md5().toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        return cipher.doFinal(data)
    }

    /**
     * AES256  解密
     */
    fun decrypt(sSrc: String, key: String, ivm: String = ivParameter): String {
        val raw = key.get32MD5().toByteArray(Charsets.US_ASCII)
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val iv = IvParameterSpec(ivm.get16Md5().toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        val encrypted = Base64.decode(sSrc, Base64.DEFAULT)
        val original = cipher.doFinal(encrypted)
        return String(original)
    }

    fun decrypt(data: ByteArray, key: String, ivm: String = ivParameter): ByteArray {
        val raw = key.get32MD5().toByteArray(Charsets.US_ASCII)
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val iv = IvParameterSpec(ivm.get16Md5().toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        return cipher.doFinal(data)
    }

}