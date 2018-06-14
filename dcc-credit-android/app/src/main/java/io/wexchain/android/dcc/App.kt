package io.wexchain.android.dcc

import android.support.annotation.VisibleForTesting
import android.support.multidex.MultiDexApplication
import android.view.ContextThemeWrapper
import com.wexmarket.android.network.Networking
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.wexchain.android.common.Pop
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.network.CommonApi
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.repo.PassportRepository
import io.wexchain.android.dcc.repo.ScfTokenManager
import io.wexchain.android.dcc.repo.db.PassportDatabase
import io.wexchain.android.dcc.tools.JuzixData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.WxApiManager
import io.wexchain.dccchainservice.*
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.api.*
import io.wexchain.digitalwallet.proxy.*
import java.lang.ref.WeakReference
import java.math.BigInteger

/**
 * Created by sisel on 2018/3/27.
 */
class App : MultiDexApplication() {

    //lib
    lateinit var idVerifyHelper: IdVerifyHelper

    @VisibleForTesting
    lateinit var networking: Networking

    lateinit var commonApi: CommonApi

    //our services
    lateinit var chainGateway: ChainGateway
    lateinit var certApi: CertApi
    lateinit var chainFrontEndApi: ChainFrontEndApi
    lateinit var privateChainApi: PrivateChainApi
    lateinit var marketingApi: MarketingApi
    lateinit var scfApi: ScfApi

    //public services
    lateinit var infuraApi: InfuraApi
    lateinit var customPublicJsonRpc: EthJsonRpcApi
    lateinit var etherScanApi: EtherScanApi
    lateinit var ethplorerApi: EthplorerApi
    lateinit var publicRpc: EthsRpcAgent


    lateinit var passportRepository: PassportRepository
    lateinit var assetsRepository: AssetsRepository
    lateinit var scfTokenManager: ScfTokenManager

    override fun onCreate() {
        super.onCreate()
        instance = WeakReference(this)
        val themeWrapper = ContextThemeWrapper(this, io.wexchain.dcc.R.style.DccLightTheme_App)
        RxJavaPlugins.setErrorHandler {
            val ex = it.cause ?: it
            if (ex is DccChainServiceException && !ex.message.isNullOrBlank()) {
                Pop.toast(it.message!!, themeWrapper)
            }
            if (BuildConfig.DEBUG) it.printStackTrace()
        }
        initLibraries(this)
        initServices(this)
        initData(this)
    }

    private fun initData(app: App) {
        val dao = PassportDatabase.createDatabase(this).dao
        passportRepository = PassportRepository(app, dao)
        passportRepository.load()
        assetsRepository = AssetsRepository(
                dao,
                chainFrontEndApi,
                EthereumAgent(publicRpc, EthsTxAgent.by(etherScanApi)),
                { buildAgent(it) }
        )
        scfTokenManager = ScfTokenManager(app)
        CertOperations.init(app)
        JuzixData.init(app)
        LocalProtect.init(app)
    }

    private fun initServices(app: App) {
        val networking = Networking(app, BuildConfig.DEBUG)
        this.networking = networking
        this.commonApi = networking.createApi(CommonApi::class.java,"https://www.google.com")//url is unused

        chainGateway = networking.createApi(ChainGateway::class.java, BuildConfig.GATEWAY_BASE_URL)
        certApi = networking.createApi(CertApi::class.java, BuildConfig.CHAIN_FUNC_URL)
        chainFrontEndApi = networking.createApi(ChainFrontEndApi::class.java, BuildConfig.CHAIN_FRONTEND_URL)
        privateChainApi = networking.createApi(PrivateChainApi::class.java, BuildConfig.CHAIN_EXPLORER_URL)
        marketingApi = networking.createApi(MarketingApi::class.java, BuildConfig.DCC_MARKETING_API_URL)
        scfApi = networking.createApi(ScfApi::class.java,BuildConfig.DCC_MARKETING_API_URL)

        etherScanApi = networking.createApi(EtherScanApi::class.java, EtherScanApi.apiUrl(Chain.publicEthChain))
        ethplorerApi = networking.createApi(EthplorerApi::class.java, EthplorerApi.API_URL)
//        infuraApi = networking.createApi(InfuraApi::class.java, InfuraApi.getUrl)
//        publicRpc = EthsRpcAgent.by(infuraApi)
        customPublicJsonRpc = networking.createApi(EthJsonRpcApi::class.java, cc.sisel.ewallet.BuildConfig.PUBLIC_CHAIN_RPC).getPrepared()
        publicRpc = EthsRpcAgent.by(customPublicJsonRpc)
    }

    private fun initLibraries(context: App) {
        idVerifyHelper = IdVerifyHelper(context)
        WxApiManager.init()
    }

    private fun buildAgent(dc: DigitalCurrency): Erc20Agent {
        return when (dc.chain) {
            Chain.JUZIX_PRIVATE -> {
                dc.contractAddress!!
                val privateRpc = networking.createApi(EthJsonRpcApi::class.java, EthJsonRpcApi.juzixErc20RpcUrl(BuildConfig.GATEWAY_BASE_URL,dc.symbol))
                        .getPrepared()
                JuzixErc20Agent(dc, EthsRpcAgent.by(privateRpc), txAgentBy(privateChainApi, dc))
            }
            Chain.publicEthChain -> {
                val contractAddress = dc.contractAddress!!
                Erc20Agent(dc, publicRpc, EthsTxAgent.Companion.by(ethplorerApi, contractAddress))
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun txAgentBy(privateChainApi: PrivateChainApi, dc: DigitalCurrency) = object : EthsTxAgent {
        override fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>> {
            val contractAddress = dc.contractAddress!!
            return privateChainApi.tokenTransfer(contractAddress, address)
                    .compose(Result.checked())
                    .map {
                        it.items.map {
                            EthsTransaction(
                                    digitalCurrency = dc,
                                    txId = it.transactionHash,
                                    from = it.fromAddress,
                                    to = it.toAddress,
                                    amount = BigInteger(it.value),
                                    transactionType = EthsTransaction.TYPE_TRANSFER,
                                    time = it.blockTimestamp,
                                    blockNumber = it.blockNumber,
                                    gas = BigInteger.ZERO,
                                    gasPrice = BigInteger.ZERO,
                                    gasUsed = BigInteger.ZERO,
                                    status = EthsTransaction.Status.MINED
                            )
                        }
                    }
        }
    }


    companion object {


        private lateinit var instance: WeakReference<App>

        fun get(): App = instance.get()!!
    }

}