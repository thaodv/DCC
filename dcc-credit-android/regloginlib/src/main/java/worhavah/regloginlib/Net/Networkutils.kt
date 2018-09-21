package worhavah.regloginlib.Net

import android.app.Application
import android.content.Context
import com.wexmarket.android.network.Networking
import io.reactivex.Single
import io.wexchain.android.common.UrlManage
import io.wexchain.android.common.kotlin.weak
import io.wexchain.android.dcc.repo.ScfTokenManager
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.api.*
import io.wexchain.digitalwallet.proxy.*
import worhavah.regloginlib.AssetsRepository
import worhavah.regloginlib.Net.beans.PrivateChainApi
import worhavah.regloginlib.PassportRepository
import java.io.File
import java.math.BigInteger

/**
 *
 */
object Networkutils {

    var  context: Context? by weak()
    lateinit var chainGateway: ChainGateway
    lateinit var networking :Networking
    lateinit var passportRepository: PassportRepository
    lateinit var scfApi: ScfApi
    lateinit var scfTokenManager: ScfTokenManager
    lateinit var assetsRepository: AssetsRepository
    lateinit var chainFrontEndApi: ChainFrontEndApi
    lateinit var publicRpc: EthsRpcAgent
    lateinit var customPublicJsonRpc: EthJsonRpcApi
    lateinit var etherScanApi: EtherScanApi
    lateinit var ethplorerApi: EthplorerApi
    lateinit var privateChainApi: PrivateChainApi
    lateinit var filesDir: File

    // val BaseRns="http://10.65.209.21:9414/rns-frontend/"//test
    //val BaseRnsUrl="http://10.65.178.12:9414/rns-frontend/"//dev

    //init
    @Synchronized
    fun  letinit(context: Application): Networkutils {
        if(null == Networkutils.context){
            Networkutils.context =context
            networking =  Networking(context, true)
            initServices(context)
            filesDir=context.filesDir
            PassportRepository.letinit(context)
        //   JuzixData.init(context)
        }
        return Networkutils
    }


    private fun initServices(app: Context) {



        passportRepository = PassportRepository.letinit(app)
        passportRepository.load()
        chainGateway = networking.createApi(ChainGateway::class.java, UrlManage.GATEWAY_BASE_URL)
        chainFrontEndApi = networking.createApi(ChainFrontEndApi::class.java, UrlManage.CHAIN_FRONTEND_URL)
        scfApi = networking.createApi(ScfApi::class.java,UrlManage.BaseRnsUrl)
        customPublicJsonRpc = networking.createApi(EthJsonRpcApi::class.java, cc.sisel.ewallet.BuildConfig.PUBLIC_CHAIN_RPC).getPrepared()
        publicRpc = EthsRpcAgent.by(customPublicJsonRpc)
        etherScanApi = networking.createApi(EtherScanApi::class.java, EtherScanApi.apiUrl(Chain.publicEthChain))
        scfTokenManager = ScfTokenManager(app)
        privateChainApi = networking.createApi(PrivateChainApi::class.java, UrlManage.CHAIN_EXPLORER_URL)
        ethplorerApi = networking.createApi(EthplorerApi::class.java, EthplorerApi.API_URL)
        assetsRepository = AssetsRepository(
                chainFrontEndApi,
                EthereumAgent(publicRpc, EthsTxAgent.by(etherScanApi)),
                { buildAgent(it) }
        )
    }


    private fun buildAgent(dc: DigitalCurrency): Erc20Agent {
        return when (dc.chain) {
            Chain.JUZIX_PRIVATE -> {
                dc.contractAddress!!
                val privateRpc = networking.createApi(EthJsonRpcApi::class.java, EthJsonRpcApi.juzixErc20RpcUrl(UrlManage.GATEWAY_BASE_URL,dc.symbol))
                        .getPrepared()
                JuzixErc20Agent(dc, EthsRpcAgent.by(privateRpc), txAgentBy(privateChainApi, dc))
            }
            Chain.publicEthChain -> {
                val contractAddress = dc.contractAddress!!
                Erc20Agent(dc, publicRpc, EthsTxAgent.by(ethplorerApi, contractAddress))
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


}