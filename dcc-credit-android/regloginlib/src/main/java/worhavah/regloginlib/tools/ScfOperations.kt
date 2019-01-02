package worhavah.regloginlib.tools

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import worhavah.regloginlib.Net.ChainGateway
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.PassportRepository
import java.math.BigDecimal

object ScfOperations {

    fun loadHolding(): Single<BigDecimal> {
        val passport = PassportRepository.getCurrentPassport()
        return if (passport != null) {
            val dccPrivate = MultiChainHelper.getDccPrivate()
            Networkutils.assetsRepository.getDigitalCurrencyAgent(dccPrivate)
                    .getBalanceOf(passport.address)
                    .map {
                        dccPrivate.toDecimalAmount(it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            Single.error<BigDecimal>(IllegalStateException())
        }
    }

}
