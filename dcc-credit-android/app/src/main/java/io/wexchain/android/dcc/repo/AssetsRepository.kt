package io.wexchain.android.dcc.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Single
import io.wexchain.android.common.map
import io.wexchain.android.common.zipLiveData
import io.wexchain.android.dcc.repo.db.PassportDao
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.ChainFrontEndApi
import io.wexchain.digitalwallet.api.domain.front.CResult
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.proxy.Erc20Agent
import io.wexchain.digitalwallet.proxy.EthCurrencyAgent
import io.wexchain.digitalwallet.proxy.EthereumAgent
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AssetsRepository(
        dao: PassportDao,
        val chainFrontEndApi: ChainFrontEndApi,
        ethereumAgent: EthereumAgent,
        val erc20AgentBuilder: (DigitalCurrency) -> Erc20Agent) {

    val pinned = MutableLiveData<List<DigitalCurrency>>()

    private val selectedCurrencies: LiveData<List<DigitalCurrency>> = dao.listCurrencyMeta(true).map {
        it?.map { it.toDigitalCurrency() } ?: listOf()
    }

    val displayCurrencies: LiveData<List<DigitalCurrency>> = zipLiveData(pinned, selectedCurrencies) { a, b -> a + b }

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

    fun getBalance(dc: DigitalCurrency, address: String): Single<BigInteger> {
        return if (dc.chain == Chain.MultiChain) {
            Single.zip(MultiChainHelper.dispatch(dc).map {
                check(it.chain != Chain.MultiChain)
                getBalance(it, address)
            }, {
                println(Arrays.toString(it))
                it.fold(BigInteger.ZERO) { acc, v ->
                    acc + v as BigInteger
                }
            })
        } else {
            getDigitalCurrencyAgent(dc)
                    .getBalanceOf(address)
        }
    }

    fun getQuotes(vararg symbols: String): Single<List<Quote>> {
        return chainFrontEndApi.getQuotes(symbols.joinToString(","))
                .compose(CResult.checkedAllowingNull<List<Quote>>(emptyList(), true))
                .doOnSuccess {
                    it.forEach {
                        it.computeChange()
                        quoteCache[it.varietyCode] = it
                    }
                }
                .onErrorReturn {
                    symbols.mapNotNull {
                        quoteCache[it]
                    }
                }
    }


    companion object {

        val preset = listOf<DigitalCurrency>(
                DigitalCurrency("EOS", Chain.publicEthChain, 18, "EOS", "http://www.wexpass.cn/images/Contractz_icon/eos@2x.png", "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0"),
                DigitalCurrency("TRX", Chain.publicEthChain, 6, "TRON", "http://www.wexpass.cn/images/Contractz_icon/tron@2x.png", "0xf230b790e05390fc8295f4d3f60332c93bed42e2"),
                DigitalCurrency("QTUM", Chain.publicEthChain, 18, "Qtum", "http://www.wexpass.cn/images/Contractz_icon/qtum@2x.png", "0x9a642d6b3368ddc662CA244bAdf32cDA716005BC"),
                DigitalCurrency("RED", Chain.publicEthChain, 18, "Red Community", "http://www.wexpass.cn/images/Contractz_icon/red_community_token@2x.png", "0x76960Dccd5a1fe799F7c29bE9F19ceB4627aEb2f"),
                DigitalCurrency("RUFF", Chain.publicEthChain, 18, "RUFF", "http://www.wexpass.cn/images/Contractz_icon/ruff@2x.png", "0xf278c1ca969095ffddded020290cf8b5c424ace2"),
                DigitalCurrency("AIDOC", Chain.publicEthChain, 18, "AI Doctor", "http://www.wexpass.cn/images/Contractz_icon/ai_doctor@2x.png", "0x584b44853680ee34a0f337b712a8f66d816df151"),
                DigitalCurrency("QUN", Chain.publicEthChain, 18, "QunQun", "http://www.wexpass.cn/images/Contractz_icon/qunqun@2x.png", "0x264dc2dedcdcbb897561a57cba5085ca416fb7b4")
        )
    }
}