package io.wexchain.android.dcc.chain

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import io.wexchain.dccchainservice.DccChainServiceException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException

/**
 *Created by liuyang on 2018/7/18.
 */
object IPFSHelp {

    private lateinit var ipfs: IPFS

    /**
     * Application init
     */
    fun init(host: String) {
        doAsync {
            ipfs = IPFS(host)
            try {
                ipfs.refs.local()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    /**
     * Add a file or directory to IPFS.
     */
    fun upload(path: String, onError: ((DccChainServiceException) -> Unit) = {}, onSuccess: ((String) -> Unit)) {
        val file = File(path)
        if (!file.exists()) {
            onError.invoke(DccChainServiceException("Files or folders do not exist"))
            return
        }
        doAsync {
            val filewrapper = NamedStreamable.FileWrapper(file)
            try {
                val addResult = ipfs.add(filewrapper)
                addResult?.forEach {
                    val base58 = it.hash.toBase58()
                    uiThread {
                        onSuccess.invoke(base58)
                    }
                }
            } catch (e: IOException) {
                uiThread {
                    onError.invoke(DccChainServiceException(e.message))
                }
            }
        }
    }

    /**
     * IPFS download directory or file
     */
    fun download(base58: String, onError: ((DccChainServiceException) -> Unit) = {}, onData: ((ByteArray) -> Unit), isDirectory:Boolean = false) {
        doAsync {
            val filePointer = Multihash.fromBase58(base58)
            try {
                if (isDirectory){
                    val mutableList = ipfs.ls(filePointer)
                    mutableList.forEach {
                        val fileContents = ipfs.cat(it.hash)
                        uiThread {
                            onData.invoke(fileContents)
                        }
                    }
                }else{
                    val fileContents = ipfs.cat(filePointer)
                    uiThread {
                        onData.invoke(fileContents)
                    }
                }

            } catch (e: IOException) {
                uiThread {
                    onError.invoke(DccChainServiceException(e.message))
                }
            }
        }
    }
}