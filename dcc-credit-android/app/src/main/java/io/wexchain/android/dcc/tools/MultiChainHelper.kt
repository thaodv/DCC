package io.wexchain.android.dcc.tools

import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import java.util.concurrent.ConcurrentHashMap

object MultiChainHelper {
    private val multiDcMap = ConcurrentHashMap<String, List<DigitalCurrency>>()

    fun putMultiDc(mdc: DigitalCurrency, dispatches: List<Pair<Chain, String>>) {
        require(mdc.chain == Chain.MultiChain)
        multiDcMap[mdc.symbol] = dispatches.map {
            check(it.first != Chain.MultiChain)
            mdc.copy(chain = it.first, contractAddress = it.second)
        }
    }

    fun dispatch(mdc: DigitalCurrency): List<DigitalCurrency> {
        require(mdc.chain == Chain.MultiChain)
        return multiDcMap[mdc.symbol] ?: emptyList()
    }

}