package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.PassportSettingsActivity
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.isPasswordValid
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityOpencloudBinding
import io.wexchain.ipfs.utils.doMain

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

            if (TYPE == PassportSettingsActivity.NOT_OPEN_CLOUD) {
                if (CommonUtils.checkPassword(pw)) {
                    createCloudPsw(pw)
                } else {
                    toast("输入云存储密码不符合要求,请检查")
                }
            } else {
                if (isPasswordValid(pw)) {
                    createCloudPsw(pw)
                } else {
                    toast("输入云存储密码不符合要求,请检查")
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
        if (TYPE == PassportSettingsActivity.OPEN_CLOUD) {
            IpfsOperations.checkPsw()
                    .doMain()
                    .filter {
                        val ipfsKey = passport.createIpfsKey(psw).toHex()
                        if (ipfsKey != it) {
                            toast("密码输入有误")
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
        } else if (TYPE == PassportSettingsActivity.NOT_OPEN_CLOUD) {
            IpfsOperations.putIpfsKey(psw)
                    .doMain()
                    .withLoading()
                    .subscribeBy(
                            onSuccess = {
                                navigateTo(MyCloudActivity::class.java)
                                finish()
                            },
                            onError = {
                                toast("密码写入失败")
                            })
        }
    }
}
