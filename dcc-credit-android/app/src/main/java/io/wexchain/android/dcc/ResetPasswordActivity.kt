package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.base.ActivityCollector
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.txSigned
import io.wexchain.android.dcc.network.ContractApi
import io.wexchain.android.dcc.network.sendRawTransaction
import io.wexchain.android.dcc.network.transactionReceipt
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.doMain
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.Erc20Helper
import kotlinx.android.synthetic.main.activity_resetpsw.*

/**
 *Created by liuyang on 2018/8/13.
 */
class ResetPasswordActivity : BaseCompatActivity() {

    private val passport by lazy {
        App.get().passportRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpsw)
        initToolbar()
        cloud_reset.onClick {
            val ipfsKey = Erc20Helper.deleteIpfsKey()

            val dccPublic = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
            val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccPublic)

            Singles.zip(
                    agent.getNonce(passport.getCurrentPassport()!!.address),
                    App.get().contractApi.getIpfsContractAddress(ContractApi.IPFS_KEY_HASH).check())
                    .map {
                        ipfsKey.txSigned(passport.getCurrentPassport()!!.credential, it.second, it.first)
                    }
                    .flatMap {
                        App.get().contractApi.sendRawTransaction(ContractApi.IPFS_KEY_HASH, it)
                    }
                    .flatMap {
                        App.get().contractApi.transactionReceipt(ContractApi.IPFS_KEY_HASH, it).retryWhen(RetryWithDelay.createGrowth(8, 1000))
                    }
                    .doMain()
                    .withLoading()
                    .subscribeBy {
                        passport.setIpfsKeyHash("")
                        ActivityCollector.getTaskActivity(1).finish()
                        finish()
                    }
        }
    }
}