package io.wexchain.android.dcc.modules.ipfs.service

import android.os.Binder

/**
 *Created by liuyang on 2018/8/28.
 */
class IpfsBinder(private val service: IpfsService): Binder() {

    fun upload(business: String, filename: String, status: (String, String) -> Unit, successful: (String) -> Unit, onError: (String,Throwable) -> Unit) {
        service.upload(business,filename, status, successful,onError)
    }

    fun download(business: String, filename: String, status: (String, String) -> Unit, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        service.download(business,filename, status, onSuccess,onError)
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