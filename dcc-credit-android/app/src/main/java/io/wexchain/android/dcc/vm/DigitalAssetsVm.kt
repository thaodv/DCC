package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.*
import android.databinding.ObservableArrayMap
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.map
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.CoinDetail
import io.wexchain.digitalwallet.util.toBigDecimalSafe
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/23.
 */
class DigitalAssetsVm(app: Application) : AndroidViewModel(app) {

    private val assetsRepository = getApplication<App>().assetsRepository

    private var holderAddress: LiveData<String> = getApplication<App>().passportRepository.currPassport.map {
        it?.address ?: ""
    }

    val assetsSumValue = ObservableField<String>()
    val assets = Transformations.map(assetsRepository.displayCurrencies) {
        updateHoldingAndQuote(it)
        updateSumValue()
        it
    }
    val holding = ObservableArrayMap<DigitalCurrency, BigInteger>()
    // val quote = ObservableArrayMap<String, Quote>()
    val quote = ObservableArrayMap<String, CoinDetail>()

    fun ensureHolderAddress(lifecycleOwner: LifecycleOwner) {
        this.holderAddress.observe(lifecycleOwner, Observer {})
    }

    private fun updateSumValue() {
        val list = assets.value
        val sumString = if (list == null) {
            ""
        } else {
            val sum = list
                    .map {
                        val h = holding[it]
                        val q = quote[it.symbol]
                        if (h != null && q != null && q.price != null /*&& q.symbol.equals("CNY", true)*/) {
                            it.toDecimalAmount(h) * q.price!!.toBigDecimalSafe()
                        } else {
                            BigDecimal.ZERO
                        }
                    }
                    .fold(BigDecimal.ZERO) { acc, v -> acc + v }
            "≈¥${sum.currencyToDisplayStr()}"
        }
        assetsSumValue.set(sumString)
    }

    /*fun updateHoldingAndQuote(list: List<DigitalCurrency>? = assets.value) {
        list ?: return
        //update quotes
        val qSet = list.filter {
            val expires = quote[it.symbol]?.expires()
            expires == null || expires
        }.map { it.symbol }.toSet()
        assetsRepository.getQuotes(*qSet.toTypedArray())
                .subscribeBy(
                        onSuccess = {
                            it.forEach {
                                quote[it.varietyCode] = it
                            }
                            updateSumValue()
                        },
                        onError = {
                            stackTrace(it)
                        })

        //update holding
        val holder = holderAddress.value
        if (holder == null || holder.isEmpty()) {
            holding.clear()
            updateSumValue()
        } else {
            list.forEach { dc ->
                assetsRepository.getBalance(dc, holder)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (holder == holderAddress.value) {
                                holding[dc] = it
                            }
                            updateSumValue()
                        }, {
                            stackTrace(it)
                        })
            }
        }
    }*/
    fun updateHoldingAndQuote(list: List<DigitalCurrency>? = assets.value) {
        list ?: return
        //update quotes
        val qSet = list.filter {
            val expires = quote[it.symbol]?.expires()
            expires == null || expires
        }.map { it.symbol }.toSet()
        assetsRepository.getCoinDetail(*qSet.toTypedArray())
                .subscribeBy(
                        onSuccess = {
                            it.forEach {
                                quote[it.symbol] = it
                            }
                            updateSumValue()
                        },
                        onError = {
                            stackTrace(it)
                        })

        //update holding
        val holder = holderAddress.value
        if (holder == null || holder.isEmpty()) {
            holding.clear()
            updateSumValue()
        } else {
            list.forEach { dc ->
                assetsRepository.getBalance(dc, holder)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (holder == holderAddress.value) {
                                holding[dc] = it
                            }
                            updateSumValue()
                        }, {
                            stackTrace(it)
                        })
            }
        }
    }

}
