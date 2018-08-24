package io.wexchain.ipfs.utils

import java.security.MessageDigest
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
    private const val HASH_ALGORITHM = "SHA-256"
    private const val ivParameter = "1234567890123456"

    /**
     * 将key转换SHA-256
     */
    fun tohash256Deal(datastr: ByteArray): ByteArray {
        val digester = MessageDigest.getInstance(HASH_ALGORITHM)
        digester.update(datastr)
        return digester.digest()
    }

    /**
     * byte数组转换为16进制字符串
     */
    fun bytes2Hex(bts: ByteArray): String {
        var des = ""
        var tmp: String?
        for (i in bts.indices) {
            tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
            if (tmp!!.length == 1) {
                des += "0"
            }
            des += tmp
        }
        return des
    }

    private fun getKey(key: String): String {
        val sKey = CharArray(32)
        val chars = key.toCharArray()
        for (index in sKey.indices) {
            if (index < chars.size) {
                sKey[index] = chars[index]
            }
        }
        return String(sKey)
    }

    /**
     * AES256  加密
     */
    fun encrypt(sSrc: String, key: String,ivm:String =ivParameter ): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
//        val raw = tohash256Deal(key.toByteArray())
        val raw = getKey(key).toByteArray()
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val iv = IvParameterSpec(ivm.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(sSrc.toByteArray())
        return Base64Encoder.encode(encrypted)
    }

    fun encrypt(data: ByteArray, key: String,ivm:String =ivParameter): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val raw = getKey(key).toByteArray()
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val iv = IvParameterSpec(ivm.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        return cipher.doFinal(data)
    }

    /**
     * AES256  解密
     */
    fun decrypt(sSrc: String, key: String,ivm:String =ivParameter): String {
        val raw = getKey(key).toByteArray(Charsets.US_ASCII)
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val iv = IvParameterSpec(ivm.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        val encrypted1 = Base64Decoder.decodeToBytes(sSrc)
        val original = cipher.doFinal(encrypted1)
        return String(original)
    }

    fun decrypt(data: ByteArray, key: String,ivm:String =ivParameter): ByteArray {
        val raw = getKey(key).toByteArray(Charsets.US_ASCII)
        val skeySpec = SecretKeySpec(raw, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val iv = IvParameterSpec(ivm.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        return cipher.doFinal(data)
    }
}