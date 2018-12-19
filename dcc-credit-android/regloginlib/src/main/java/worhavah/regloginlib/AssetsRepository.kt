package worhavah.regloginlib

import android.arch.lifecycle.MutableLiveData
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.proxy.Erc20Agent
import io.wexchain.digitalwallet.proxy.EthCurrencyAgent
import io.wexchain.digitalwallet.proxy.EthereumAgent
import java.util.concurrent.ConcurrentHashMap

class AssetsRepository(

        ethereumAgent: EthereumAgent,
        val erc20AgentBuilder: (DigitalCurrency) -> Erc20Agent) {

    private val pinned = MutableLiveData<List<DigitalCurrency>>().apply {
        value = listOf(Currencies.DCC, Currencies.Ethereum)
    }

    private val quoteCache = mutableMapOf<String, Quote>()

    private val dcAgentMap = ConcurrentHashMap<DigitalCurrency, EthCurrencyAgent>()

    init {
        dcAgentMap[Currencies.Ethereum] = ethereumAgent
    }

    fun getDigitalCurrencyAgent(dc: DigitalCurrency): EthCurrencyAgent {
        val agent = dcAgentMap[dc]
        return if (agent == null) {
            val contractAddress = dc.contractAddress
            if (contractAddress != null) {
                //create a new public chain erc20 agent
                val created = erc20AgentBuilder(dc)
                dcAgentMap[dc] = created
                created
            } else {
                throw IllegalArgumentException()
            }
        } else agent
    }
}
