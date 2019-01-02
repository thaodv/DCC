package io.wexchain.android.dcc.modules.mine

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.AboutActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.PassportRemovalActivity
import io.wexchain.android.dcc.modules.selectnode.SelectNodeActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.fragment.CreateProtectFragment
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivitySettingBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CheckUpgrade
import zlc.season.rxdownload3.core.Mission
import java.io.File

/**
 *Created by liuyang on 2018/9/18.
 */
class SettingActivity : BindActivity<ActivitySettingBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initClick()
    }

    private fun initVm() {
        val protect = getViewModel<Protect>()
        protect.sync(this)
        protect.protectEnableEvent.observe(this, Observer {
            CreateProtectFragment.create(object : CreateProtectFragment.CreateProtectFinishListener {
                override fun onCancel(): Boolean {
                    return true
                }

                override fun cancelText(): String {
                    return getString(R.string.cancel)
                }

                override fun onCreateProtectFinish(type: LocalProtectType) {
                    toast(R.string.local_protect_enabled)
                }

            }).show(supportFragmentManager, null)
        })
        protect.protectDisabledEvent.observe(this, Observer {
            toast(getString(R.string.local_security_is_disabled_successfully))
        })
        VerifyProtectFragment.serve(protect, this)
        binding.protect = protect
    }

    private fun initClick() {
        binding.tvAboutUs.onClick {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.tvDeletePassport.onClick {
            startActivity(Intent(this, PassportRemovalActivity::class.java))
        }
        binding.tvSelectNode.onClick {
            navigateTo(SelectNodeActivity::class.java)
        }
        binding.tvCheckUpdate.onClick {
            App.get().marketingApi.checkUpgrade(versionInfo.versionCode.toString())
                    .checkonMain()
                    .doOnSuccess {
                        showUpgradeDialog(it)
                    }
                    .doOnError {
                        if (it is DccChainServiceException) {
                            toast("当前已是最新版本")
                        }
                    }
                    .subscribeBy()
        }
    }

    private fun showUpgradeDialog(it: CheckUpgrade) {
        val dialog = BaseDialog(this)
        if (it.mandatoryUpgrade) {
            dialog.createHomeDialog(it.version, it.updateLog)
                    .onClick {
                        dialog.dismiss()
                        downloadApk(it.version, it.updateUrl)
                    }
        } else {
            dialog.createCheckDialog(it.version, it.updateLog)
                    .onClick(
                            onCancle = {
                                dialog.dismiss()
                            },
                            onConfirm = {
                                dialog.dismiss()
                                downloadApk(it.version, it.updateUrl)
                            })
        }
    }

    private fun downloadApk(versionNumber: String, updateUrl: String) {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        RxPermissions(this).request(permission)
                .filter {
                    if (!it) {
                        toast("没有读写文件权限,授权后才能下载")
                    }
                    it
                }
                .doOnComplete {
                    val savepath = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "BitExpress")
                    if (!savepath.exists()) {
                        savepath.mkdirs()
                    }
                    val filename = "BitExpress$versionNumber.apk"
                    val file = File(savepath, filename)
                    if (file.exists()) {
                        installApk(file)
                    } else {
                        val mission = Mission(updateUrl, filename, savepath.absolutePath)
                        BaseDialog(this).crateDownloadDialog(mission)
                    }
                }
                .subscribeBy()
    }
}
