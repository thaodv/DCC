package io.wexchain.android.dcc.tools

import android.content.Context
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Prefs
import io.wexchain.android.dcc.App
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies

object JuzixData {

    private lateinit var juzixPrefs: JuzixPrefs

    fun init(app: App) {
        juzixPrefs = JuzixPrefs(app)
//        app.chainGateway.getErc20ContractAddress(Currencies.FTC.symbol)
//                .compose(Result.checked())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { address ->
//                    juzixPrefs.ftcContractAddress.set(address)
//                    update()
//                }
        MultiChainHelper.putMultiDc(Currencies.DCC, listOf(
                Chain.publicEthChain to cc.sisel.ewallet.BuildConfig.DCC_PUBLIC_ADDRESS,
                Chain.JUZIX_PRIVATE to juzixPrefs.dccContractAddress.get()!!
        ))
        updateDccJuzixAddress(app).subscribe()
        updateBsxAddress(app).subscribe()
    }

    private fun updateDccJuzixAddress(app: App): Single<String> {
        return app.chainGateway.getErc20ContractAddress(Currencies.DCC.symbol)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { address ->
                    juzixPrefs.dccContractAddress.set(address)
                    update()
                }
    }
    private fun updateBsxAddress(app: App): Single<String> {
        return app.chainGateway.getBiintContractAddress( )
             .compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { address ->
                BintApi.contract=address
            }
    }
    private fun update() {
        MultiChainHelper.putMultiDc(Currencies.DCC, listOf(
                Chain.publicEthChain to cc.sisel.ewallet.BuildConfig.DCC_PUBLIC_ADDRESS,
                Chain.JUZIX_PRIVATE to juzixPrefs.dccContractAddress.get()!!
        ))
//        val ftc = Currencies.FTC.copy(contractAddress = juzixPrefs.ftcContractAddress.get()!!)
        App.get().assetsRepository.setPinnedList( listOf(
                Currencies.DCC,
//                ftc,
                Currencies.Ethereum
        ))
    }

    private const val JUZIX_DATA_PREFS = "juzix_data_prefs"

    class JuzixPrefs(context: Context) : Prefs(context.getSharedPreferences(JUZIX_DATA_PREFS, Context.MODE_PRIVATE)) {
        val ftcContractAddress = StringPref("contract_address_juzix_ftc", cc.sisel.ewallet.BuildConfig.QX_CONTRACT_ADDRESS)
        val dccContractAddress = StringPref("contract_address_juzix_dcc", cc.sisel.ewallet.BuildConfig.DCC_JUZIX_CONTRACT_ADDRESS)
    }
}