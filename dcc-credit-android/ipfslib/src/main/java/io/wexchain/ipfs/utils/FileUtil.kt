package io.wexchain.ipfs.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *Created by liuyang on 2018/8/7.
 */

fun ByteArray.writeToFile(filePath: String, fileName: String): String {
    val bos: BufferedOutputStream?
    val fos: FileOutputStream?
    val file: File?
    val dir = File(filePath)
    if (!dir.exists() && dir.isDirectory) {
        dir.mkdirs()
    }
    file = File(filePath + File.separator + fileName)
    if (file.exists()) {
        file.delete()
    }
    fos = FileOutputStream(file)
    bos = BufferedOutputStream(fos)
    bos.write(this)
    bos.close()
    fos.close()
    return file.absolutePath

}

fun File.readToString(): String {
    val filelength = this.length()
    val filecontent = ByteArray(filelength.toInt())
    val input = FileInputStream(this)
    input.read(filecontent)
    input.close()
    return String(filecontent)
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

/*fun <T> String.toBean(classOf: Class<T>): T {
    return IpfsCore.GSON.fromJson<T>(this, classOf)
}*/

fun String.base64(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}