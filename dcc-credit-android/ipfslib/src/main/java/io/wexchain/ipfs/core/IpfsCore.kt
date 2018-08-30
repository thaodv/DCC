package io.wexchain.ipfs.core

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.ipfs.utils.doBack
import java.io.File

/**
 *Created by liuyang on 2018/8/3.
 */
object IpfsCore {

    private lateinit var ipfs: IPFS

    /**
     * Application init
     * @param host ipfs地址
     */
    fun init(host: String, port: Int) {
        Single.just(Pair(host, port))
                .doBack()
                .subscribeBy {
                    ipfs = IPFS(it.first, it.second)
                    ipfs.refs.local()
                }
    }

    fun init(host: String) {
        Single.just(host)
                .doBack()
                .subscribeBy {
                    ipfs = IPFS(it)
                    ipfs.refs.local()
                }
    }

    /**
     * Add a file to IPFS.
     * @param path 需要上传文件的路径
     */
    fun upload(path: String): Single<String> {
        return Single.just(path)
                .flatMap {
                    val file = File(path)
                    if (file.isFile) {
                        if (!file.exists()) {
                            Single.error(Throwable("File do not exist"))
                        } else {
                            Single.just(file)
                        }
                    } else {
                        Single.error(Throwable("Path Not is File"))
                    }

                }
                .map {
                    val filewrapper = NamedStreamable.FileWrapper(it)
                    val result = ipfs.add(filewrapper)
                    result[0]
                }
                .map {
                    it.hash.toBase58()
                }
    }

    /**
     * IPFS download file
     * @param base58 ipfs文件Token
     */
    fun download(base58: String): Single<ByteArray> {
        return Single.just(base58)
                .flatMap {
                    val filePointer = Multihash.fromBase58(it)
                    val data = ipfs.cat(filePointer)
                    Single.just(data)
                }
    }
}