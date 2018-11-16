package io.wexchain.android.dcc.modules.ipfs.service

import android.os.Binder
import io.reactivex.Single
import io.wexchain.android.dcc.modules.ipfs.EventType

/**
 *Created by liuyang on 2018/8/28.
 */
class IpfsBinder(private val service: IpfsService): Binder() {

    fun upload(business: String, filename: String, status: (String, EventType) -> Unit, successful: (String) -> Unit, onError: (String, Throwable) -> Unit, onProgress: (String, Int) -> Unit) {
        service.upload(business,filename, status, successful,onError,onProgress)
    }

    fun download(business: String, filename: String, status: (String, EventType) -> Unit, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit, onProgress: (String,Int) -> Unit) {
        service.download(business,filename, status, onSuccess,onError,onProgress)
    }

    fun createItemData(business: String): Single<String> {
       return service.createItemData(business)
    }
}