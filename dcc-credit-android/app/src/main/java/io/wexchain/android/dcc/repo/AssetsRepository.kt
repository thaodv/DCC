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
import io.wexchain.digitalwallet.api.CoinMarketCapApi
import io.wexchain.digitalwallet.api.domain.front.CResult
import io.wexchain.digitalwallet.api.domain.front.CoinDetail
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.proxy.Erc20Agent
import io.wexchain.digitalwallet.proxy.EthCurrencyAgent
import io.wexchain.digitalwallet.proxy.EthereumAgent
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap

class AssetsRepository(
        val dao: PassportDao,
        val chainFrontEndApi: ChainFrontEndApi,
        val coinMarketCapApi: CoinMarketCapApi,
        ethereumAgent: EthereumAgent,
        val erc20AgentBuilder: (DigitalCurrency) -> Erc20Agent) {

    private val dcAgentMap = ConcurrentHashMap<DigitalCurrency, EthCurrencyAgent>()

    init {
        dcAgentMap[Currencies.Ethereum] = ethereumAgent
    }

    private val pinned = MutableLiveData<List<DigitalCurrency>>().apply {
        value = listOf(
                Currencies.DCC,
                Currencies.Ethereum
        )
    }

    /**
     * user selected digital currencies
     * [pinned] not included
     */
    val selectedCurrencies: LiveData<List<DigitalCurrency>> = dao.listCurrencyMeta(true).map {
        it?.map { it.toDigitalCurrency() } ?: emptyList()
    }

    fun getCurrencyMeta(symbol: String): CurrencyMeta{

        return dao.getCurrencyMeta(symbol)
    }

    /**
     * digital currencies list to be shown in ui
     * concat [pinned] and [selectedCurrencies]
     */
    val displayCurrencies: LiveData<List<DigitalCurrency>> = zipLiveData(pinned, selectedCurrencies) { a, b -> a + b }

    private val quoteCache = mutableMapOf<String, Quote>()

    private val coinDetailCache = mutableMapOf<String, CoinDetail>()

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
            getDigitalCurrencyAgent(dc).getBalanceOf(address)
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

    fun getCoinDetail(vararg symbols: String): Single<List<CoinDetail>> {
        return coinMarketCapApi.coinMarketCap(symbols.joinToString(","))
                .compose(CResult.checkedAllowingNull<List<CoinDetail>>(emptyList(), true))
                .doOnSuccess {
                    it.forEach {
                        coinDetailCache[it.symbol] = it
                    }
                }
                .onErrorReturn {
                    symbols.mapNotNull {
                        coinDetailCache[it]
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
        val pinned = pinned.value ?: emptyList()
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
        val pinned = this.pinned.value ?: emptyList()
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
        removePendingTx("", ethsTransaction.nonce)
        pendingTxList.value = (pendingTxList.value ?: emptyList()).toMutableList().apply {
            add(0, ethsTransaction)
        }
    }

    @MainThread
    fun removePendingTx(txId: String) {
        pendingTxList.value = (pendingTxList.value ?: emptyList()).filter { it.txId != txId }
    }

   /* @MainThread
    fun removePendingTx(txId: String, nonce: BigInteger) {
        pendingTxList.value = (pendingTxList.value
                ?: emptyList()).filter { it.txId != txId }.filter { it.nonce != nonce }
    }*/
   @MainThread
   fun removePendingTx(txId: String, nonce: BigInteger) {
       pendingTxList.value = (pendingTxList.value
               ?: emptyList()).filter {! it.txId .equals(txId) }.filter { it.nonce != nonce }
   }
    fun setPinnedList(pinnedList: List<DigitalCurrency>) {
        this.pinned.postValue(pinnedList)
    }

    fun isPinned(digitalCurrency: DigitalCurrency): Boolean {
        if (digitalCurrency.symbol == Currencies.DCC.symbol) {
            return true
        }
        return this.pinned.value?.contains(digitalCurrency) == true
    }

    /*fun putPresetCurrencies() {
        val preset = AssetsRepository.preset
        val datas = ArrayList<CurrencyMeta>()
        preset.forEach {

            datas.add(CurrencyMeta.from(it))
        }
        RoomHelper.onRoomIoThread {
            dao.addOrReplaceCurrencyMeta(datas)
        }
    }*/

    companion object {

        val preset = listOf(
                DigitalCurrency("DTA", Chain.publicEthChain, 18, "DATA", "http://open.dcc.finance/images/dapp/product_bata.png", "0x69b148395ce0015c13e36bffbad63f49ef874e03"),
                DigitalCurrency("BNB", Chain.publicEthChain, 18, "BNB", "http://open.dcc.finance/images/dapp/product_bnb.png", "0xB8c77482e45F1F44dE1745F52C74426C631bDD52"),
                DigitalCurrency("TUSD", Chain.publicEthChain, 18, "TrueUSD", "http://open.dcc.finance/images/dapp/product_tusd.png", "0x8dd5fbce2f6a956c3022ba3663759011dd51e73e"),
                DigitalCurrency("HT", Chain.publicEthChain, 18, "HuobiToken", "http://open.dcc.finance/images/dapp/product_ht.png", "0x6f259637dcd74c767781e37bc6133cd6a68aa161"),
                DigitalCurrency("EOS", Chain.publicEthChain, 18, "EOS", "http://www.wexpass.cn/images/Contractz_icon/eos@2x.png", "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0"),
                DigitalCurrency("TRX", Chain.publicEthChain, 6, "TRON", "http://www.wexpass.cn/images/Contractz_icon/tron@2x.png", "0xf230b790e05390fc8295f4d3f60332c93bed42e2"),
                DigitalCurrency("QTUM", Chain.publicEthChain, 18, "Qtum", "http://www.wexpass.cn/images/Contractz_icon/qtum@2x.png", "0x9a642d6b3368ddc662CA244bAdf32cDA716005BC"),
                DigitalCurrency("RED", Chain.publicEthChain, 18, "Red Community", "http://www.wexpass.cn/images/Contractz_icon/red_community_token@2x.png", "0x76960Dccd5a1fe799F7c29bE9F19ceB4627aEb2f"),
                DigitalCurrency("RUFF", Chain.publicEthChain, 18, "RUFF", "http://www.wexpass.cn/images/Contractz_icon/ruff@2x.png", "0xf278c1ca969095ffddded020290cf8b5c424ace2"),
                DigitalCurrency("AIDOC", Chain.publicEthChain, 18, "AI Doctor", "http://www.wexpass.cn/images/Contractz_icon/ai_doctor@2x.png", "0x584b44853680ee34a0f337b712a8f66d816df151"),
                DigitalCurrency("ICX", Chain.publicEthChain, 18, "ICON", "http://open.dcc.finance/images/token_icon/ICX@2x.png", "0xb5a5f22694352c15b00323844ad545abb2b11028"),
                DigitalCurrency("ITC", Chain.publicEthChain, 18, "IOT Chain", "http://open.dcc.finance/images/token_icon/ITC@2x.png", "0x5e6b6d9abad9093fdc861ea1600eba1b355cd940"),
                DigitalCurrency("KNC", Chain.publicEthChain, 18, "KyberNetwork", "http://open.dcc.finance/images/token_icon/KNC@2x.png", "0xdd974d5c2e2928dea5f71b9825b8b646686bd200"),
                DigitalCurrency("LINK", Chain.publicEthChain, 18, "ChainLink Token", "http://open.dcc.finance/images/token_icon/LINK@2x.png", "0x514910771af9ca656af840dff83e8264ecf986ca"),
                DigitalCurrency("LUN", Chain.publicEthChain, 18, "Lunyr", "http://open.dcc.finance/images/token_icon/LUN@2x.png", "0xfa05A73FfE78ef8f1a739473e462c54bae6567D9"),
                DigitalCurrency("MANA", Chain.publicEthChain, 18, "Decentraland", "http://open.dcc.finance/images/token_icon/MANA@2x.png", "0x0f5d2fb29fb7d3cfee444a200298f468908cc942"),
                DigitalCurrency("MCO", Chain.publicEthChain, 18, "Monaco", "http://open.dcc.finance/images/token_icon/MCO@2x.png", "0xb63b606ac810a52cca15e44bb630fd42d8d1d83d"),
                DigitalCurrency("NAS", Chain.publicEthChain, 18, "Nebulas", "http://open.dcc.finance/images/token_icon/NAS@2x.png", "0x5d65D971895Edc438f465c17DB6992698a52318D"),
                DigitalCurrency("OMG", Chain.publicEthChain, 18, "OmiseGO", "http://open.dcc.finance/images/token_icon/OMG@2x.png", "0xd26114cd6EE289AccF82350c8d8487fedB8A0C07"),
                DigitalCurrency("QSP", Chain.publicEthChain, 18, "Quantstamp", "http://open.dcc.finance/images/token_icon/QSP@2x.png", "0x99ea4db9ee77acd40b119bd1dc4e33e1c070b80d"),
                DigitalCurrency("VEN", Chain.publicEthChain, 18, "VenChain", "http://open.dcc.finance/images/token_icon/VEN@2x.png", "0xd850942ef8811f2a866692a623011bde52a462c1"),
                DigitalCurrency("ZRX", Chain.publicEthChain, 18, "ZRX", "http://open.dcc.finance/images/token_icon/ZRX@2x.png", "0xe41d2489571d322189246dafa5ebde1f4699f498"),
                DigitalCurrency("BAT", Chain.publicEthChain, 18, "BAT", "http://open.dcc.finance/images/token_icon/BAT@2x.png", "0x0d8775f648430679a709e98d2b0cb6250d2887ef"),
                DigitalCurrency("CVC", Chain.publicEthChain, 18, "CVC", "http://open.dcc.finance/images/token_icon/CVC@2x.png", "0x5f08b925b2c5bcbfba0febef9ab43f6f84310bdf"),
                DigitalCurrency("ELF", Chain.publicEthChain, 18, "ELF", "http://open.dcc.finance/images/token_icon/ELF@2x.png", "0xbf2179859fc6d5bee9bf9158632dc51678a4100e"),
                DigitalCurrency("ENG", Chain.publicEthChain, 18, "Enigma", "http://open.dcc.finance/images/token_icon/ENG@2x.png", "0xf0ee6b27b759c9893ce4f094b49ad28fd15a23e4"),
                DigitalCurrency("GNT", Chain.publicEthChain, 18, "Golem", "http://open.dcc.finance/images/token_icon/GNT@2x.png", "0xa74476443119A942dE498590Fe1f2454d7D4aC0d")
                //, DigitalCurrency("QUN", Chain.publicEthChain, 18, "QunQun", "http://www.wexpass.cn/images/Contractz_icon/qunqun@2x.png", "0x264dc2dedcdcbb897561a57cba5085ca416fb7b4")
        )
    }
}
