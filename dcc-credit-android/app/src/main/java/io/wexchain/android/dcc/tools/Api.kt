package io.wexchain.android.dcc.tools

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ListView
import android.widget.ScrollView
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.ipfs.net.Networking
import io.wexchain.ipfs.utils.io_main
import org.web3j.utils.Numeric
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

/**
 *Created by liuyang on 2018/7/20.
 */

fun getString(id: Int): String {
    return App.get().resources.getString(id)
}

fun <T : Any> T.Log(message: CharSequence?) {
    val clazz = (this as java.lang.Object).`class` as Class<T>
    LogUtils.e(clazz.simpleName, message?.toString())
}

val <T : Any> T.javaClass: Class<T>
    @Suppress("UsePropertyAccessSyntax")
    get() = (this as java.lang.Object).getClass() as Class<T>

/*fun Log(tag: String, message: CharSequence) {
    LogUtils.e(tag, message.toString())
}*/

fun ByteArray.toSha256(): ByteArray {
    return MessageDigest.getInstance("SHA256").digest(this)
}

fun File.reName(newName: String) {
    FileUtils.rename(this, newName)
}

fun Any.toJson(): String {
    return Networking.Gson.toJson(this)
}

fun <T> String.toBean(classOf: Class<T>): T {
    return Networking.Gson.fromJson<T>(this, classOf)
}

fun File.formatSize(): String {
    val memorySize = FileUtils.byte2FitMemorySize(this.length())
    val split = memorySize.split('.')
    var size = ""
    for ((index, s) in split.withIndex()) {
        size += if (index == split.size - 1) {
            val tmps = split[index]
            val tmp = tmps.substring(3, tmps.length)
            tmp
        } else {
            s
        }
    }
    return size
}

fun String.fixLengthBins(): String {
    val bins = StringBuilder(this)
    val len = bins.length
    for (i in 0 until 64 - len) {
        bins.insert(0, '0')
    }
    return bins.toString()
}

fun Passport.getPrivateKey(): String {
    return Numeric.toHexStringNoPrefix(this.credential.ecKeyPair.privateKey).fixLengthBins()
}

fun String.hexToTen(): BigInteger {
    return BigInteger(this.substring(2), 16)
}

fun Window.backgroundAlpha(bgAlpha: Float) {
    val lp = this.attributes
    lp.alpha = bgAlpha
    this.attributes = lp
}

fun Bitmap.saveImageToGallery(context: Context): Single<String> {
    return Single.create<String> {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            it.onError(DccChainServiceException("SD card is not available."))
        }
        try {
            val appDir = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "BitExpress" + File.separator + "Img")
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            val fos = FileOutputStream(file)
            this.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, fileName, null)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + R.attr.path)))
            it.onSuccess(file.absolutePath)
        } catch (e: Exception) {
            it.onError(e)
        }
    }
}

fun View.onLongSaveImageToGallery(onError: (Throwable) -> Unit, onSuccess: (String) -> Unit) {
    this.setOnLongClickListener { view ->
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        view.drawingCache.saveImageToGallery(context)
                .io_main()
                .doFinally {
                    view.destroyDrawingCache()
                }
                .subscribeBy(onError, onSuccess)
        true
    }
}

fun View.onLongSaveImageToGallery(root: View, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit) {
    this.setOnLongClickListener { view ->
        val cacheView = root
        cacheView.getViewBitmap().saveImageToGallery(context)
                .io_main()
                .subscribeBy(onError, onSuccess)
        true
    }
}
fun View.onSaveImageToGallery(root: View, onError: (Throwable) -> Unit, onSuccess: (String) -> Unit) {
    this.setOnClickListener { view ->
        val cacheView = root
        cacheView.getViewBitmap().saveImageToGallery(context)
                .io_main()
                .subscribeBy(onError, onSuccess)
        true
    }
}

fun View.getViewBitmap(): Bitmap {
    val bitmap = when {
        this is ScrollView -> {
            val height = this.getChildAt(0).height
            Bitmap.createBitmap(this.width, height, Bitmap.Config.ARGB_8888)
        }
        this is ListView -> {
            var height = 0
            for (i in 0..this.childCount) {
                height += this.getChildAt(i).height
            }
            Bitmap.createBitmap(this.width, height, Bitmap.Config.ARGB_8888)
        }
        this is RecyclerView -> {
            var height = 0
            for (i in 0..this.childCount) {
                height += this.getChildAt(i).height
            }
            Bitmap.createBitmap(this.width, height, Bitmap.Config.ARGB_8888)
        }
        else -> Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    }
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

fun isRootSystem(): Boolean {
    var f: File? = null
    val kSuSearchPaths = arrayOf("/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/")
    try {
        for ((i, j) in kSuSearchPaths.withIndex()) {
            f = File(j + "su")
            if (f.exists()) {
                return true
            }
        }
    } catch (e: Exception) {
    }
    return false
}

fun Long.formatText(): String {
    val minute: Long = 60 * 1000
    val hour: Long = 60 * minute
    val day: Long = 24 * hour
    val month: Long = 31 * day
    val year: Long = 12 * month

    val date = Date(this)

    val diff = Date().time - date.time;
    var r: Long = 0
    if (diff > year) {
        r = (diff / year)
        return "$r 年前"
    }
    if (diff > month) {
        r = (diff / month)
        return "$r 个月前"
    }
    if (diff > day) {
        r = (diff / day)
        return "$r 天前"
    }
    if (diff > hour) {
        r = (diff / hour)
        return "$r 个小时前"
    }
    if (diff > minute) {
        r = (diff / minute)
        return "$r 分钟前"
    }
    return "刚刚"

}

fun View.onNoDoubleClick(click: (View) -> Unit) {
    val view = this
    view.setOnClickListener(object : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View?) {
            click(view)
        }
    })

}

fun EditText.fixPrice() {
    val view = this
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let {
                var res = it
                if (res.contains(".")) {
                    if (res.length - 1 - res.indexOf(".") > 2) {
                        res = res.substring(0, res.indexOf(".") + 3)
                        view.setText(res)
                        view.setSelection(res.length)
                    }
                }

                //如果.在起始位置,则起始位置自动补0
                if (res.trim().substring(0) == ".") {
                    res = "0$res";
                    view.setText(res)
                    view.setSelection(2)
                }

                //如果起始位置为0并且第二位跟的不是".",则无法后续输入
                if (res.startsWith("0") && res.trim().length > 1) {
                    if (res.substring(1, 2) != ".") {
                        view.setText(res.subSequence(0, 1))
                        view.setSelection(1)
                        return
                    }
                }
            }
        }
    })
}

fun isRoot(): Boolean {
    val file1 = File("/system/bin/su")
    val file2 = File("/system/xbin/su")
    return file1.exists() || file2.exists()
}

fun checkXPosed(): Boolean {
    return try {
        val localObject = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedHelpers").newInstance()
        // 如果加载类失败 则表示当前环境没有xposed
        true
    } catch (localThrowable: Throwable) {
        false
    }

}

