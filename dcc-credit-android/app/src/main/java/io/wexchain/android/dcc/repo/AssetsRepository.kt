package io.wexchain.android.dcc.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread
import io.reactivex.Single
import io.wexchain.android.common.map
import io.wexchain.android.common.zipLiveData
import io.wexchain.android.dcc.repo.db.CurrencyMeta
import io.wexchain.android.dcc.repo.db.PassportDao
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RoomHelper
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
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
        val dao: PassportDao,
        val chainFrontEndApi: ChainFrontEndApi,
        ethereumAgent: EthereumAgent,
        val erc20AgentBuilder: (DigitalCurrency) -> Erc20Agent) {

    private val pinned = MutableLiveData<List<DigitalCurrency>>().apply {
        value = listOf(
                Currencies.DCC,
                Currencies.Ethereum
        )
    }

    val selectedCurrencies: LiveData<List<DigitalCurrency>> = dao.listCurrencyMeta(true).map {
        it?.map { it.toDigitalCurrency() } ?: emptyList()
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

    fun queryTokens(query: String): Single<List<DigitalCurrency>> {
        return chainFrontEndApi.searchToken(query)
                .compose(CResult.checked())
                .map {
                    it.items?.map { it.toDigitalCurrency() } ?: emptyList()
                }.doOnSuccess {
                    //                    knownCurrencies.addAll(it)
                }
    }

    fun addSelected(dc: DigitalCurrency) {
        val pinned = pinned.value?: emptyList()
        if (!pinned.contains(dc) || dc.contractAddress != null) {
            RoomHelper.onRoomIoThread {
                if (dao.addSelected(CurrencyMeta.from(dc)) > 0) {
//                    ld(LOG_TAG, "addSelected successful $dc")
                } else {
//                    ld(LOG_TAG, "addSelected fail $dc")
                }
            }
        } else {
//            ld(LOG_TAG, "addSelected but in pinned or contractAddress null")
        }
    }

    fun setCurrencySelected(dc: DigitalCurrency, sel: Boolean) {
        val pinned = this.pinned.value?: emptyList()
        if (!pinned.contains(dc) || dc.contractAddress != null) {
            RoomHelper.onRoomIoThread {
                dao.updateCurrencyMeta(CurrencyMeta.from(dc, sel))
            }
        }
    }

    val pendingTxList = MutableLiveData<List<EthsTransaction>>().apply {
        value = emptyList()
    }

    @MainThread
    fun pushPendingTx(ethsTransaction: EthsTransaction) {
        pendingTxList.value = (pendingTxList.value ?: emptyList()).toMutableList().apply {
            add(0, ethsTransaction)
        }
    }

    @MainThread
    fun removePendingTx(txId: String) {
        pendingTxList.value = (pendingTxList.value ?: emptyList()).filter { it.txId != txId }
    }

    fun setPinnedList(pinnedList: List<DigitalCurrency>) {
        this.pinned.postValue(pinnedList)
    }

    fun isPinned(digitalCurrency: DigitalCurrency): Boolean {
        if (digitalCurrency.symbol == Currencies.DCC.symbol){
            return true
        }
        return this.pinned.value?.contains(digitalCurrency) == true
    }

    companion object {

        val preset = listOf<DigitalCurrency>(
                DigitalCurrency("DATA", Chain.publicEthChain, 18, "DATA", "http://open.dcc.finance/images/dapp/product_bata.png", "0x69b148395ce0015c13e36bffbad63f49ef874e03"),
                DigitalCurrency("BNB", Chain.publicEthChain, 18, "BNB", "http://open.dcc.finance/images/dapp/product_bnb.png", "0xB8c77482e45F1F44dE1745F52C74426C631bDD52"),
                DigitalCurrency("TUSD", Chain.publicEthChain, 18, "TrueUSD", "http://open.dcc.finance/images/dapp/product_tusd.png", "0x8dd5fbce2f6a956c3022ba3663759011dd51e73e"),
                DigitalCurrency("HT", Chain.publicEthChain, 18, "HuobiToken", "http://open.dcc.finance/images/dapp/product_ht.png", "0x6f259637dcd74c767781e37bc6133cd6a68aa161"),
                DigitalCurrency("EOS", Chain.publicEthChain, 18, "EOS", "http://www.wexpass.cn/images/Contractz_icon/eos@2x.png", "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0"),
                DigitalCurrency("TRX", Chain.publicEthChain, 6, "TRON", "http://www.wexpass.cn/images/Contractz_icon/tron@2x.png", "0xf230b790e05390fc8295f4d3f60332c93bed42e2"),
                DigitalCurrency("QTUM", Chain.publicEthChain, 18, "Qtum", "http://www.wexpass.cn/images/Contractz_icon/qtum@2x.png", "0x9a642d6b3368ddc662CA244bAdf32cDA716005BC"),
                DigitalCurrency("RED", Chain.publicEthChain, 18, "Red Community", "http://www.wexpass.cn/images/Contractz_icon/red_community_token@2x.png", "0x76960Dccd5a1fe799F7c29bE9F19ceB4627aEb2f"),
                DigitalCurrency("RUFF", Chain.publicEthChain, 18, "RUFF", "http://www.wexpass.cn/images/Contractz_icon/ruff@2x.png", "0xf278c1ca969095ffddded020290cf8b5c424ace2"),
                DigitalCurrency("AIDOC", Chain.publicEthChain, 18, "AI Doctor", "http://www.wexpass.cn/images/Contractz_icon/ai_doctor@2x.png", "0x584b44853680ee34a0f337b712a8f66d816df151")
                //, DigitalCurrency("QUN", Chain.publicEthChain, 18, "QunQun", "http://www.wexpass.cn/images/Contractz_icon/qunqun@2x.png", "0x264dc2dedcdcbb897561a57cba5085ca416fb7b4")
        )
    }
}