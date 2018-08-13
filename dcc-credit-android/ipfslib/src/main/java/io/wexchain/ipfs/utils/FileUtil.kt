package io.wexchain.ipfs.utils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 *Created by liuyang on 2018/8/7.
 */

fun ByteArray.writeToFile(filePath: String, fileName: String): String? {
    var bos: BufferedOutputStream? = null
    var fos: FileOutputStream? = null
    val file: File?
    try {
        val dir = File(filePath)
        if (!dir.exists() && dir.isDirectory) {
            dir.mkdirs()
        }
        file = File(filePath + File.separator + fileName)
        fos = FileOutputStream(file)
        bos = BufferedOutputStream(fos)
        bos.write(this)
        return file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        bos?.close()
        fos?.close()
    }
}