package io.wexchain.ipfs.utils

import android.util.Base64
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 *Created by liuyang on 2018/8/7.
 */

fun String.base64(): ByteArray {
    return Base64.decode(this, Base64.NO_WRAP)
}

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String.get16Md5(): String {
    return this.get32MD5().substring(8, 24)
}

fun String.get32MD5(): String {
    val md = MessageDigest.getInstance("MD5")
    md.update(this.toByteArray())
    val md5 = BigInteger(1, md.digest()).toString(16)
    return fillMD5(md5).toUpperCase()
}

fun fillMD5(md5: String): String {
    return if (md5.length == 32) md5 else fillMD5("0$md5")
}


