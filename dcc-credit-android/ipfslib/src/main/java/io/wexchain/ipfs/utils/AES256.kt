package io.wexchain.ipfs.utils

import java.security.Key
import java.security.MessageDigest
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * AES 数据加密解密
 * Created by liuyang on 2018/8/6.
 */
object AES256 {

    private const val KEY_ALGORITHM = "AES"
    private const val CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding"
    private const val HASH_ALGORITHM = "SHA-256"

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

    /**
     * 生成Key
     */
    private fun toKey(key: ByteArray): Key {
        return SecretKeySpec(key, KEY_ALGORITHM)
    }

    /**
     * AES 数据加密
     * @param data 待加密数据
     * @param key  加密密码
     * @return 已加密数据
     */
    fun encrypt(data: ByteArray, key: String): ByteArray {
        val bytes = tohash256Deal(key.toByteArray())
        val k = toKey(bytes)
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, k)
        return cipher.doFinal(data)

    }

    /**
     * AES 数据解密
     * @param data 待解密数据
     * @param key  解密密码
     * @return 已解密数据
     */
    fun decrypt(data: ByteArray, key: String): ByteArray {
        val bytes = tohash256Deal(key.toByteArray())
        val k = toKey(bytes)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC")
        cipher.init(Cipher.DECRYPT_MODE, k)
        return cipher.doFinal(data)
    }
}