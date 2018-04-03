package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.reactivex.Flowable
import io.wexchain.android.common.map
import io.wexchain.android.common.switchMap
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.AutoLoadLiveData
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.api.domain.front.Quote
import java.math.BigInteger

class DccExchangeVm(application: Application):AndroidViewModel(application) {

    val holder = (application as App).passportRepository.currPassport.map { it?.address }
    val dcc = Currencies.DCC

    val juzixHolding = holder.switchMap {address->
        AutoLoadLiveData<BigInteger>{
            if (address == null){
                Flowable.just(BigInteger.ZERO)
            }else{
                val dJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
                getApplication<App>().assetsRepository.getBalance(dJuzix,address).toFlowable()
            }
        }
    }

    val publicHolding = holder.switchMap {address->
        AutoLoadLiveData<BigInteger>{
            if (address == null){
                Flowable.just(BigInteger.ZERO)
            }else{
                val dPublic = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
                getApplication<App>().assetsRepository.getBalance(dPublic,address).toFlowable()
            }
        }
    }

    val quote = AutoLoadLiveData<Quote>{
        val symbol = Currencies.DCC.symbol
        getApplication<App>().assetsRepository.getQuotes(symbol)
                .map {
                    it.firstOrNull { it.quoteSymbol == symbol }!!
                }
                .toFlowable()
    }

}