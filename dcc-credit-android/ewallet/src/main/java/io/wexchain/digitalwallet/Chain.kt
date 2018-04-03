package io.wexchain.digitalwallet

import cc.sisel.ewallet.BuildConfig

/**
 * Created by sisel on 2018/1/18.
 */
enum class Chain {
    /**
     * Ethereum main net
     */
    Ethereum,
    /**
     * Rinkeby test net
     */
    Rinkeby,
    /**
     * private chain
     * with several tokens
     */
    JUZIX_PRIVATE,

    /**
     * indicates the digital currency exists on multiple chain
     * eg: DCC exist on both [JUZIX_PRIVATE] and [Ethereum]
     */
    MultiChain
    ;

    companion object {
        val publicEthChain = Chain.valueOf(BuildConfig.PUBLIC_CHAIN)
    }
}