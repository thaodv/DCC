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
     * private chain
     * with several tokens
     */
    JUZIX_PRIVATE,
    /**
     * Rinkeby test net
     */
    Rinkeby
    ;

    companion object {
        val publicEthChain = Chain.valueOf(BuildConfig.PUBLIC_CHAIN)
    }
}