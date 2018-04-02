package io.wexchain.dccchainservice.util

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

fun ByteArray.toHex():String {
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