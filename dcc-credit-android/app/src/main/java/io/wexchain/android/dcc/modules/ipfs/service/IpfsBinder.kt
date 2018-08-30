package io.wexchain.android.dcc.modules.ipfs.service

import android.os.Binder

/**
 *Created by liuyang on 2018/8/28.
 */
class IpfsBinder(private val service: IpfsService): Binder() {

    fun upload(business: String, filename: String, status: (String, String) -> Unit, successful: (String) -> Unit) {
        service.upload(business,filename, status, successful)
    }

    fun download(business: String, filename: String, status: (String, String) -> Unit, successful: (String) -> Unit) {
        service.download(business,filename, status, successful)
    }

    fun createIdData(onSize:(String)->Unit){
        service.createIdData(onSize)
    }

    fun createBankData(onSize:(String)->Unit){
        service.createBankData(onSize)
    }

    fun createCmData(onSize:(String)->Unit){
        service.createCmData(onSize)
    }

}