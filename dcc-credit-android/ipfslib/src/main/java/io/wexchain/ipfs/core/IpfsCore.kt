package io.wexchain.ipfs.core

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.wexchain.ipfs.entity.IpfsVersion
import io.wexchain.ipfs.net.IpfsApi
import io.wexchain.ipfs.net.Networking
import io.wexchain.ipfs.utils.doMain
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 *Created by liuyang on 2018/9/3.
 */
object IpfsCore {

    private lateinit var baseUrl: String
    private val api: IpfsApi by lazy {
        Networking.createApi(IpfsApi::class.java)
    }

    fun init(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    fun creatUrl(host: String, port: String) = "http://$host:$port/api/v0/"

    fun download(token: String): Single<ByteArray> {
        val url = baseUrl + "cat?arg=$token"
        return api.download(url)
                .map {
                    it.bytes()
                }
                .subscribeOn(Schedulers.io())
    }

    fun upload(file: File): Single<String> {
        val body = creatBody(file)
        val url = baseUrl + "add?"
        return api.upload(url, body)
                .map {
                    it.Hash
                }
                .subscribeOn(Schedulers.io())
    }

    fun checkVersion(host: String, port: String): Single<IpfsVersion> {
        val url = creatUrl(host, port) + "version?number=false&commit=false&repo=false&all=false"
        return api.getVersion(url)
                .subscribeOn(Schedulers.io())
                .doMain()
    }

    private fun creatBody(file: File): MultipartBody {
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        requestBody.addFormDataPart("file", file.name, RequestBody.create(null, file))
        return requestBody.build()
    }

}