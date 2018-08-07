package io.wexchain.ipfs.utils

import java.io.*
import java.util.zip.*

/**
 * 文件数据压缩
 * Created by liuyang on 2018/8/3.
 */
object ZipUtil {

    /**
     * 压缩方法
     * @param src 需要压缩的文件的绝对路径
     * @param archive 输出压缩包的路径名字
     * @param comment 压缩描述
     */
    fun zip(archive: String, comment: String,vararg src: String) {
        val f = FileOutputStream(archive)
        val csum = CheckedOutputStream(f, CRC32())
        val zos = ZipOutputStream(csum)
        val out = BufferedOutputStream(zos)
        zos.setComment(comment)
        zos.setMethod(ZipOutputStream.DEFLATED)
        zos.setLevel(Deflater.BEST_COMPRESSION)
        for (i in src.indices) {
            val srcFile = File(src[i])
            if (!srcFile.exists() || srcFile.isDirectory && srcFile.list().isEmpty()) {
                throw FileNotFoundException()
            }
            var strSrcString = src[i]
            strSrcString = strSrcString.replace("////".toRegex(), "/")
            var prefixDir: String?
            prefixDir = if (srcFile.isFile) {
                strSrcString.substring(0, strSrcString.lastIndexOf("/") + 1)
            } else {
                strSrcString.replace("/$".toRegex(), "") + "/"
            }
            writeRecursive(zos, out, srcFile, prefixDir)
        }
        out.close()
    }


    /**
     * 解压方法
     * @param archive 压缩包的路径
     * @param decompressDir 解压路径
     */
    fun unZip(archive: String, decompressDir: String) {
        var bi: BufferedInputStream
        val zf = ZipFile(archive)
        val e = zf.entries()
        while (e.hasMoreElements()) {
            val ze2 = e.nextElement() as ZipEntry
            val entryName = ze2.name
            val path = "$decompressDir/$entryName"
            if (ze2.isDirectory) {
                val decompressDirFile = File(path)
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs()
                }
            } else {
                val fileDir = path.substring(0, path.lastIndexOf("/"))
                val fileDirFile = File(fileDir)
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs()
                }
                val bos = BufferedOutputStream(
                        FileOutputStream("$decompressDir/$entryName"))
                bi = BufferedInputStream(zf.getInputStream(ze2))
                val readContent = ByteArray(1024)
                var readCount = bi.read(readContent)
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount)
                    readCount = bi.read(readContent)
                }
                bos.close()
            }
        }
        zf.close()
    }

    private fun writeRecursive(zos: ZipOutputStream, bo: BufferedOutputStream, srcFile: File, prefixDir: String) {
        val zipEntry: ZipEntry
        var filePath = srcFile.absolutePath.replace("////".toRegex(), "/")
                .replace("//".toRegex(), "/")
        if (srcFile.isDirectory) {
            filePath = filePath.replace("/$".toRegex(), "") + "/"
        }
        val entryName = filePath.replace(prefixDir, "").replace("/$".toRegex(), "")
        if (srcFile.isDirectory) {
            if ("" != entryName) {
                zipEntry = ZipEntry("$entryName/")
                zos.putNextEntry(zipEntry)
            }
            val srcFiles = srcFile.listFiles()
            for (i in srcFiles.indices) {
                writeRecursive(zos, bo, srcFiles[i], prefixDir)
            }
        } else {
            val bi = BufferedInputStream(FileInputStream(srcFile))
            zipEntry = ZipEntry(entryName)
            zos.putNextEntry(zipEntry)
            val buffer = ByteArray(1024)
            var readCount = bi.read(buffer)
            while (readCount != -1) {
                bo.write(buffer, 0, readCount)
                readCount = bi.read(buffer)
            }
            bo.flush()
            bi.close()
        }
    }
}
