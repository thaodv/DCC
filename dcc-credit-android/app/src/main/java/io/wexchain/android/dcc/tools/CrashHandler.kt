package io.wexchain.android.dcc.tools

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.SystemClock
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 *Created by liuyang on 2018/7/24.
 */
class CrashHandler : Thread.UncaughtExceptionHandler {
    private var mContext: Context? = null

    override fun uncaughtException(t: Thread, ex: Throwable) {
        dumpExceptionToSDCard(ex)
        SystemClock.sleep(1000)
        Process.killProcess(Process.myPid())
    }

    fun init(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context.applicationContext
    }

    private fun dumpExceptionToSDCard(ex: Throwable) {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return
        }
        val dir = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "BitExpress" + File.separator + "Log")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val current = System.currentTimeMillis()
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(current))
        val file = File(dir.absolutePath, FILE_NAME + time + FILE_NAME_SUFFIX)
        try {
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            val pm = mContext!!.packageManager
            val pi = pm.getPackageInfo(mContext!!.packageName, PackageManager.GET_ACTIVITIES)
            pw.println("应用版本：" + pi.versionName)
            pw.println("应用版本号：" + pi.versionCode)
            pw.println("android版本号：" + Build.VERSION.RELEASE)
            pw.println("android版本号API：" + Build.VERSION.SDK_INT)
            pw.println("手机制造商:" + Build.MANUFACTURER)
            pw.println("手机型号：" + Build.MODEL)
            ex.printStackTrace(pw)
            pw.close()
        } catch (e: Exception) {

        }
    }


    companion object {
        private const val FILE_NAME = "crash"
        private const val FILE_NAME_SUFFIX = ".trace"
    }
}
