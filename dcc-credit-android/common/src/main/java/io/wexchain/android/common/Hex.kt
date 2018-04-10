package io.wexchain.android.common

import android.text.TextUtils
import android.util.SparseIntArray
import kotlin.experimental.or

private val hexCharsLower = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
)

private val numHex = SparseIntArray(hexCharsLower.size).apply {
    val chars = hexCharsLower
    val length = chars.size
    for (i in 0 until length) {
        put(chars[i].toInt(), i)
    }
}

fun ByteArray.toHex(): String {
    return String(this.toHexChars())
}

fun ByteArray.toHexChars(): CharArray {
    val len = this.size
    val out = CharArray(len shl 1)
    var j = 0
    this.forEach { byte ->
        out[j++] = hexCharsLower[(0xf0 and byte.toInt()) ushr 4]
        out[j++] = hexCharsLower[(0x0f and byte.toInt())]
    }
    return out
}

@Throws(IllegalArgumentException::class)
fun String.hexStrToBytes(): ByteArray {
    val hex = this
    if (TextUtils.isEmpty(hex) || hex.length % 2 != 0) {
        throw IllegalArgumentException()
    }
    val len = hex.length
    val chars = hex.toLowerCase().toCharArray()
    val result = ByteArray(len shr 1)
    var by: Byte
    var i = 0
    while (i < len) {
        by = 0
        by = by or (numHex.get(chars[i].toInt()) shl 4).toByte()
        by = by or numHex.get(chars[i + 1].toInt()).toByte()
        result[i shr 1] = by
        i += 2
    }
    return result
}