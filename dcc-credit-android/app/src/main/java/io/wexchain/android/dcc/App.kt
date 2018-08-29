package io.wexchain.android.dcc

import android.content.Context
import android.os.SystemClock
import android.support.annotation.VisibleForTesting
import android.support.multidex.MultiDex
import android.view.ContextThemeWrapper
import com.wexmarket.android.network.Networking
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.wexchain.android.common.BaseApplication
import io.wexchain.android.common.Pop
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.network.CommonApi
import io.wexchain.android.dcc.network.IpfsApi
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.repo.PassportRepository
import io.wexchain.android.dcc.repo.ScfTokenManager
import io.wexchain.android.dcc.repo.db.PassportDatabase
import io.wexchain.android.dcc.tools.CrashHandler
import io.wexchain.android.dcc.tools.JuzixData
import io.wexchain.android.dcc.tools.Log
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
import io.wexchain.ipfs.core.IpfsCore
import zlc.season.rxdownload3.core.DownloadConfig
import java.lang.ref.WeakReference
import java.math.BigInteger

/**
 * Created by sisel on 2018/3/27.
 */
class App : BaseApplication(), Thread.UncaughtExceptionHandler {

    //lib
    lateinit var idVerifyHelper: IdVerifyHelper

    @VisibleForTesting
    lateinit var networking: Networking

    lateinit var commonApi: CommonApi

    //our services
    lateinit var chainGateway: ChainGateway
    lateinit var contractApi: IpfsApi
    lateinit var certApi: CertApi
    lateinit var chainFrontEndApi: ChainFrontEndApi
    lateinit var privateChainApi: PrivateChainApi
    lateinit var marketingApi: MarketingApi
    lateinit var scfApi: ScfApi
    lateinit var coinMarketCapApi: CoinMarketCapApi

    //public services
    lateinit var etherScanApi: EtherScanApi
    lateinit var ethplorerApi: EthplorerApi
    lateinit var publicRpc: EthsRpcAgent


    lateinit var passportRepository: PassportRepository
    lateinit var assetsRepository: AssetsRepository
    lateinit var scfTokenManager: ScfTokenManager

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

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
        initIpfs()
    }

    private fun initIpfs() {
        val hostStatus = passportRepository.getIpfsHostStatus()
        if (hostStatus) {
            IpfsCore.init(BuildConfig.IPFS_HOST)
        } else {
            val urlConfig = passportRepository.getIpfsUrlConfig()
            IpfsCore.init(urlConfig.first!!, urlConfig.second!!.toInt())
        }
    }

    private fun initRxDownload() {
        val builder = DownloadConfig.Builder.create(this)
                .enableAutoStart(true)
                .enableDb(true)
                .enableNotification(true)

        DownloadConfig.init(builder)
    }


    private fun initData(app: App) {
        val dao = PassportDatabase.createDatabase(this).dao

        passportRepository = PassportRepository(app, dao)
        passportRepository.load()
        assetsRepository = AssetsRepository(
                dao,
                chainFrontEndApi, coinMarketCapApi,
                EthereumAgent(publicRpc, EthsTxAgent.by(etherScanApi)),
                ::buildAgent
        )

        //App.get().assetsRepository.putPresetCurrencies()

        scfTokenManager = ScfTokenManager(app)
        CertOperations.init(app)
        JuzixData.init(app)
        LocalProtect.init(app)
    }

    private fun initServices(app: App) {
        val networking = Networking(app, BuildConfig.DEBUG)
        this.networking = networking
        this.commonApi = networking.createApi(CommonApi::class.java, "https://www.google.com")//url is unused

        chainGateway = networking.createApi(ChainGateway::class.java, "http://10.65.209.13:9511/passport-gateway/restful/")
        certApi = networking.createApi(CertApi::class.java, BuildConfig.CHAIN_FUNC_URL)
        chainFrontEndApi = networking.createApi(ChainFrontEndApi::class.java, BuildConfig.CHAIN_FRONTEND_URL)
        privateChainApi = networking.createApi(PrivateChainApi::class.java, BuildConfig.CHAIN_EXPLORER_URL)
        marketingApi = networking.createApi(MarketingApi::class.java, BuildConfig.DCC_MARKETING_API_URL)
        scfApi = networking.createApi(ScfApi::class.java, BuildConfig.DCC_MARKETING_API_URL)
        coinMarketCapApi = networking.createApi(CoinMarketCapApi::class.java, BuildConfig.COIN_MARKET)
        contractApi = networking.createApi(IpfsApi::class.java, IpfsApi.BASE_URL)

        etherScanApi = networking.createApi(EtherScanApi::class.java, EtherScanApi.apiUrl(Chain.publicEthChain))
        ethplorerApi = networking.createApi(EthplorerApi::class.java, EthplorerApi.API_URL)
        publicRpc = if (BuildConfig.DEBUG) {
            val infuraApi = networking.createApi(InfuraApi::class.java, InfuraApi.getUrl)
            EthsRpcAgent.by(infuraApi)
        } else {
//        val infuraApi = networking.createApi(InfuraApi::class.java, InfuraApi.getUrl)
//        publicRpc = EthsRpcAgent.by(infuraApi)
//            val customPublicJsonRpc = networking.createApi(EthJsonRpcApi::class.java, EthJsonRpcApi.PUBLIC_RPC_URL).getPrepared()
//            publicRpc = EthsRpcAgent.by(customPublicJsonRpc)
            val wfJsonRpc = networking.createApi(EthJsonRpcApiWithAuth::class.java, EthJsonRpcApiWithAuth.RPC_URL)
            EthsRpcAgent.by(wfJsonRpc)
        }
    }

    private fun initLibraries(context: App) {
        idVerifyHelper = IdVerifyHelper(context)
        WxApiManager.init()
        initRxDownload()
        CrashHandler().init(context)
    }

    private fun buildAgent(dc: DigitalCurrency): Erc20Agent {
        return when (dc.chain) {
            Chain.JUZIX_PRIVATE -> {
                dc.contractAddress!!
                val privateRpc = networking.createApi(EthJsonRpcApi::class.java, EthJsonRpcApi.juzixErc20RpcUrl(BuildConfig.GATEWAY_BASE_URL, dc.symbol))
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
                                    status = EthsTransaction.Status.MINED,
                                    nonce = BigInteger.ZERO
                            )
                        }
                    }
        }
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        Thread(Runnable { Log("currentThread:" + Thread.currentThread() + "---thread:" + thread.id + "---ex:" + ex.toString()) }).start()
        SystemClock.sleep(2000)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    companion object {
        private lateinit var instance: WeakReference<App>
        @JvmStatic
        fun get(): App = instance.get()!!
    }

}
