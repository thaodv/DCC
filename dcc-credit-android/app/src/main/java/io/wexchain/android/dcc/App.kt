package io.wexchain.android.dcc

import android.support.multidex.MultiDexApplication
import io.wexchain.android.idverify.IdVerifyHelper

/**
 * Created by sisel on 2018/3/27.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initLibraries(this)
    }

    lateinit var idVerifyHelper: IdVerifyHelper

    private fun initLibraries(context: App) {
        idVerifyHelper = IdVerifyHelper(context)
    }


}