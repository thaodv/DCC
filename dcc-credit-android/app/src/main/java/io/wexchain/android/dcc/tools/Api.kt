package io.wexchain.android.dcc.tools

import android.app.ActivityManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.EditText
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.ipfs.net.Networking
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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

fun Context.getProcessName(): String? {
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningApps = am.runningAppProcesses ?: return null
    return runningApps
            .firstOrNull { it.pid == android.os.Process.myPid() && it.processName != null }
            ?.processName
}

fun String.hexToTen(): BigInteger {
    return BigInteger(this.substring(2), 16)
}

fun Window.backgroundAlpha(bgAlpha: Float) {
    val lp = this.attributes
    lp.alpha = bgAlpha
    this.attributes = lp
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


