package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.PassportSettingsActivity
import io.wexchain.android.dcc.ResetPasswordActivity
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.txSigned
import io.wexchain.android.dcc.network.ContractApi
import io.wexchain.android.dcc.network.sendRawTransaction
import io.wexchain.android.dcc.network.transactionReceipt
import io.wexchain.android.dcc.tools.*
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityOpencloudBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.Erc20Helper

/**
 *Created by liuyang on 2018/8/13.
 */
class OpenCloudActivity : BindActivity<ActivityOpencloudBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_opencloud

    private val passport by lazy {
        App.get().passportRepository
    }

    private val TYPE by lazy {
        intent.getIntExtra("activity_type", -1)
    }
    private var hint = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        if (TYPE == PassportSettingsActivity.OPEN_CLOUD) {
            title = "我的云存储"
            hint = "请输入8-20位云存储密码"
            binding.ivDescribe.text = "您的钱包已开启数据云存储功能，请输入云存储密码以便将云端存储的数据下载到手机本地。"
            binding.tvLoadMore.text = "忘记密码"
            binding.tvLoadMore.onClick {
                navigateTo(ResetPasswordActivity::class.java)
            }
        } else if (TYPE == PassportSettingsActivity.NOT_OPEN_CLOUD) {
            title = "开启云存储"
            hint = "设置8-20位云存储密码"
            binding.ivDescribe.text = "您需要设置云存储密码以开启数据备份功能。云存储密码丢失后无法找回，请妥善保管。"
            binding.tvLoadMore.text = "了解更多"
            binding.tvLoadMore.onClick {
                CloudstorageDialog(this).createHelpDialog()
            }
        }
        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set(hint)
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw ?: return@setOnClickListener
            if (isPasswordValid(pw)) {
                createCloudPsw(pw)
            } else {
                toast("输入云存储密码不符合要求,请检查")
            }
        }
    }

    private fun createCloudPsw(psw: String) {
        if (null == passport.getCurrentPassport()) {
            toast("fail to get passport info")
            finish()
            return
        }

        val sha265Key = passport.createIpfsKey(psw)
        val ipfsKey = Erc20Helper.putIpfsKey(sha265Key)

        val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)

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
                    passport.setIpfsKeyHash(sha265Key.toHex())
                    navigateTo(MyCloudActivity::class.java)
                    finish()
                }
    }
}