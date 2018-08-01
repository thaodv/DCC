package io.wexchain.android.dcc.chain

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 *Created by liuyang on 2018/7/18.
 */
object IPFSHelp {

    private lateinit var ipfs: IPFS

    /**
     * Application init
     */
    fun init(host: String) {
        Single.just(host)
                .observeOn(Schedulers.io())
                .subscribeBy {
                    ipfs = IPFS(it)
                    ipfs.refs.local()
                }


    }

    /**
     * Add a file to IPFS.
     */
    fun upload(path: String): Observable<String> {
        return Observable.just(path)
                .flatMap {
                    val file = File(path)
                    if (!file.exists()) {
                        Observable.error(Throwable("Files or folders do not exist"))
                    } else {
                        Observable.just(file)
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    val filewrapper = NamedStreamable.FileWrapper(it)
                    val result = ipfs.add(filewrapper)
                    Observable.fromIterable(result)
                }
                .map {
                    it.hash.toBase58()
                }
    }

    /**
     * IPFS download file
     */
    fun download(base58: String): Single<ByteArray> {
        return Single.just(base58)
                .observeOn(Schedulers.io())
                .flatMap {
                    val filePointer = Multihash.fromBase58(it)
                    val data = ipfs.cat(filePointer)
                    Single.just(data)
                }
    }
}