package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.CoinDetail
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.util.toBigDecimalSafe
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/24.
 */
class DigitalCurrencyVm(application: Application) : AndroidViewModel(application) {

    private val assetsRepository = getApplication<App>().assetsRepository

    private lateinit var address: String

    val dc = ObservableField<DigitalCurrency>()

    val selected = ObservableBoolean()

    val pinned = ObservableBoolean()

    val quote = ObservableField<Quote>()

    val coinDetail = ObservableField<CoinDetail>()

    val holding = ObservableField<BigInteger>()

    val holdingStr = ObservableField<String>()
    val holdingValueStr = ObservableField<String>()

    val chainText = ObservableField<String>()

    val fetchDataFailEvent = SingleLiveEvent<Void>()
    val selectedChangedEvent = SingleLiveEvent<Boolean>()
    val confirmEvent = SingleLiveEvent<Pair<CharSequence, (Boolean) -> Unit>>()

    fun ensure(address: String, dc: DigitalCurrency, lifecycleOwner: LifecycleOwner) {
        this.address = address
        this.dc.set(dc)
        observeSelected(lifecycleOwner)
        pinned.set(assetsRepository.isPinned(dc))
        if (showChainText(dc)) {
            when (dc.chain) {
                Chain.JUZIX_PRIVATE -> {
                    chainText.set(getApplication<Application>().getString(R.string.on_chain_dcc))
                }
                Chain.publicEthChain -> {
                    chainText.set(getApplication<Application>().getString(R.string.on_chain_ethereum))
                }
                else -> chainText.set(null)
            }
        } else {
            chainText.set(null)
        }
    }

    private fun showChainText(dc: DigitalCurrency): Boolean {
//        return dc.copy(chain = Chain.MultiChain,contractAddress = null) == Currencies.DCC
        return true
    }

    private fun observeSelected(lifecycleOwner: LifecycleOwner) {
        assetsRepository.selectedCurrencies.observe(lifecycleOwner, Observer {
            selected.set(it?.contains(dc.get()) ?: false)
        })
    }

    fun load() {
        getCoinDetail()
        loadHolding()
    }

    fun switchSelected() {
        val digitalCurrency = dc.get()
        digitalCurrency ?: return
        val sl = !selected.get()
        if (sl) {
            assetsRepository.setCurrencySelected(digitalCurrency, sl)
            selectedChangedEvent.value = sl
        } else {
            confirmEvent.value = "确认删除${digitalCurrency.symbol}?" to { confirmed ->
                if (confirmed) {
                    assetsRepository.setCurrencySelected(digitalCurrency, sl)
                    selectedChangedEvent.value = sl
                }
            }
        }
    }

    private fun loadHolding() {
        val c = dc.get()!!
        assetsRepository.getDigitalCurrencyAgent(c)
                .getBalanceOf(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    holding.set(it)
                    updateHoldingStr(coinDetail.get(), it)
                }, {
                    fetchDataFailEvent.call()
                })
    }

    /*private fun updateHoldingStr(quote: Quote?, amount: BigInteger?) {
        amount ?: return
        val c = dc.get()!!
        holdingStr.set(c.toDecimalAmount(amount).currencyToDisplayStr())
        if (quote?.price == null) {
            holdingValueStr.set("--")
        } else {
            holdingValueStr.set("≈${quote.currencySymbol}${(c.toDecimalAmount(amount) * quote.price!!.toBigDecimalSafe()).currencyToDisplayStr()}")
        }
    }*/

    private fun updateHoldingStr(coinDetail: CoinDetail?, amount: BigInteger?) {
        amount ?: return
        val c = dc.get()!!
        holdingStr.set(c.toDecimalAmount(amount).currencyToDisplayStr())
        if (coinDetail?.price == null) {
            holdingValueStr.set("--")
        } else {
            holdingValueStr.set("≈${coinDetail.symbol}${(c.toDecimalAmount(amount) * coinDetail.price!!.toBigDecimalSafe()).currencyToDisplayStr()}")
        }
    }


    /*private fun loadQuote() {
        val symbol = dc.get()!!.symbol
        assetsRepository.getQuotes(symbol)
                .subscribe({
                    it.firstOrNull { it.varietyCode == symbol }?.let {
                        quote.set(it)
                        updateHoldingStr(it, holding.get())
                    }
                }, {
                    fetchDataFailEvent.call()
                })
    }*/

    private fun getCoinDetail(){
        val symbol = dc.get()!!.symbol
        assetsRepository.getCoinDetail(symbol)
                .subscribe({
                    it.firstOrNull { it.symbol == symbol }?.let {
                        coinDetail.set(it)
                        updateHoldingStr(it, holding.get())
                    }
                }, {
                    fetchDataFailEvent.call()
                })
    }
}
