package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.tools.CommonUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.tools.isPasswordValid
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityOpencloudBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.type.TaskCode
import io.wexchain.ipfs.utils.doMain

/**
 *Created by liuyang on 2018/8/13.
 */
class OpenCloudActivity : BindActivity<ActivityOpencloudBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_opencloud

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
        if (TYPE == SettingActivity.OPEN_CLOUD) {
            title = getString(R.string.open_ipfs_text1)
            hint = getString(R.string.open_ipfs_text2)
            binding.ivDescribe.text = getString(R.string.open_ipfs_text3)
            binding.tvLoadMore.text = getString(R.string.open_ipfs_text4)
            binding.tvLoadMore.onClick {
                navigateTo(ResetPasswordActivity::class.java)
            }
        } else if (TYPE == SettingActivity.NOT_OPEN_CLOUD) {
            title = getString(R.string.open_ipfs_text5)
            hint = getString(R.string.open_ipfs_text6)
            binding.ivDescribe.text = getString(R.string.open_ipfs_text7)
            binding.tvLoadMore.text = getString(R.string.open_ipfs_text8)
            binding.tvLoadMore.onClick {
                CloudstorageDialog(this).createHelpDialog2()
            }
        }
        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set(hint)
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw ?: return@setOnClickListener

            if (TYPE == SettingActivity.NOT_OPEN_CLOUD) {
                if (CommonUtils.checkPassword(pw)) {
                    createCloudPsw(pw)
                } else {
                    toast(getString(R.string.toast_ipfs_msg1))
                }
            } else {
                if (isPasswordValid(pw)) {
                    createCloudPsw(pw)
                } else {
                    toast(getString(R.string.toast_ipfs_msg1))
                }
            }

        }
    }

    private fun createCloudPsw(psw: String) {
        if (null == passport.getCurrentPassport()) {
            toast("fail to get passport info")
            finish()
            return
        }
        if (TYPE == SettingActivity.OPEN_CLOUD) {
            IpfsOperations.checkPsw()
                    .doMain()
                    .filter {
                        val ipfsKey = passport.createIpfsKey(psw).toHex()
                        if (ipfsKey != it) {
                            toast(getString(R.string.toast_ipfs_msg2))
                        } else {
                            val aesKey = passport.createIpfsAESKey(psw).toHex()
                            passport.setIpfsKeyHash(it)
                            passport.setIpfsAESKey(aesKey)
                        }
                        ipfsKey == it
                    }
                    .subscribeBy {
                        navigateTo(MyCloudActivity::class.java)
                        finish()
                    }
        } else if (TYPE == SettingActivity.NOT_OPEN_CLOUD) {
            IpfsOperations.putIpfsKey(psw)
                    .flatMap {
                        GardenOperations.completeTask(TaskCode.OPEN_CLOUD_STORE)
                    }
                    .doMain()
                    .withLoading()
                    .doOnError {
                        if (it is DccChainServiceException) {
                            toast(it.message!!)
                        }
                    }
                    .subscribeBy {
                        navigateTo(MyCloudActivity::class.java)
                        finish()
                    }
        }
    }
}
