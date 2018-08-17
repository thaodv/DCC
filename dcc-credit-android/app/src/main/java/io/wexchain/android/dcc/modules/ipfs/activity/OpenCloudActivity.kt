package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.network.ContractApi
import io.wexchain.android.dcc.network.sendRawTransaction
import io.wexchain.android.dcc.network.transactionCount
import io.wexchain.android.dcc.network.transactionReceipt
import io.wexchain.android.dcc.tools.*
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityOpencloudBinding
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.util.gweiTowei
import org.web3j.abi.FunctionEncoder
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

/**
 *Created by liuyang on 2018/8/13.
 */
class OpenCloudActivity : BindActivity<ActivityOpencloudBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_opencloud

    private val currentpassport by lazy {
        App.get().passportRepository.getCurrentPassport()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set("设置8-20位云存储密码")
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw ?: return@setOnClickListener
            if (isPasswordValid(pw)) {
                createCloudPsw(pw)
//                getCloudToken()
//                navigateTo(MyCloudActivity::class.java)
            } else {
                toast("设置云存储密码不符合要求,请检查")
            }
        }
        binding.tvLoadMore.onClick {
            CloudstorageDialog(this).createHelpDialog()
//            navigateTo(ResetPasswordActivity::class.java)
        }
    }

    private fun createCloudPsw(psw: String) {
        /*val response = App.get().publicRpc.transactionReceipt("0xac9e319755452590327c316ab35c2383ad44f6e377e372f91061cfd70601c7fc")
                .retryWhen(RetryWithDelay.createGrowth(8, 1000))
                .blockingGet()
        log(response.toString())*/

        if (null == currentpassport) {
            toast("fail to get passport info")
            finish()
            return
        }

        val key = currentpassport!!.credential.ecKeyPair.privateKey
        val privateKey = Numeric.toHexStringNoPrefix(key)
        val sha265Key = CertOperations.digestIdName(privateKey, psw)
        val ipfsKey = Erc20Helper.putIpfsKey(sha265Key)
//        val lastNounce = TransHelper.getLastNounce()
//        val count =  App.get().publicRpc.getTransactionCount(currentpassport!!.address,"latest").blockingGet()
//        val count = App.get().scfApi.getNonce().check().blockingGet()
//        val nonce = privateChainNonce(currentpassport!!.address)
        Singles.zip(
                App.get().contractApi.transactionCount(ContractApi.IPFS_KEY_HASH, currentpassport!!.address),
                App.get().contractApi.getIpfsContractAddress(ContractApi.IPFS_KEY_HASH).check())
                .map {
                    RawTransaction.createTransaction(
                            Numeric.toBigInt(it.first),
                            gweiTowei(BigDecimal("10")),
                            BigInteger("100000"),
                            it.second,
                            BigInteger.ZERO,
                            FunctionEncoder.encode(ipfsKey)
                    )
                }
                .map {
                    Numeric.toHexString(TransactionEncoder.signMessage(it, currentpassport!!.credential))
                }
                .flatMap {
                    App.get().contractApi.sendRawTransaction(ContractApi.IPFS_KEY_HASH, it)
                }
                .flatMap {
                    App.get().contractApi.transactionReceipt(ContractApi.IPFS_KEY_HASH, it).retryWhen(RetryWithDelay.createGrowth(8, 1000))
                }
//                .withLoading()
                .subscribeBy { getCloudToken() }
    }

    private fun getCloudToken() {
        App.get().contractApi.getIpfsContractAddress(ContractApi.IPFS_KEY_HASH)
                .check()
                .map {
                    Erc20Helper.getIpfsKey(it,currentpassport!!.address)
                }
                .flatMap {
                    App.get().contractApi
                            .postCall(
                                    ContractApi.IPFS_KEY_HASH,
                                    EthJsonRpcRequestBody(
                                            method = "eth_call",
                                            params = listOf(it, "latest"),
                                            id = 1L
                                    )
                            )
                }
                .map {
                    if (it.result.equals("0x", true))
                        BigInteger.ZERO
                    else
                        Numeric.toBigInt(it.result!!)
                }
                .doMain()
                .subscribeBy(
                        onSuccess = {
                            toast(it.toString())
                            log(it.toString())
                        },
                        onError = {
                            toast(it.message.toString())
                        })
    }
}