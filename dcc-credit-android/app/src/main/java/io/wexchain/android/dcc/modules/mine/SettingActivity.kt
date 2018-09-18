package io.wexchain.android.dcc.modules.mine

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.installApk
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.versionInfo
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.dialog.UpgradeDialog
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CheckUpgrade
import kotlinx.android.synthetic.main.activity_setting.*
import zlc.season.rxdownload3.core.Mission
import java.io.File

/**
 *Created by liuyang on 2018/9/18.
 */
class SettingActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
        initClick()
    }

    private fun initView() {
        tv_current_vs.text = getString(R.string.current_version) + versionInfo.versionName
    }

    private fun initClick() {
        tv_passport_address.onClick {
            startActivity(Intent(this, PassportAddressActivity::class.java))
        }
        tv_passport_backup.onClick {
            startActivity(Intent(this, PassportExportActivity::class.java))
        }
        tv_modify_passport_password.onClick {
            startActivity(Intent(this, ModifyPassportPasswordActivity::class.java))
        }
        tv_about_us.onClick {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        tv_delete_passport.onClick {
            startActivity(Intent(this, PassportRemovalActivity::class.java))
        }
        tv_check_update.onClick {
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
        val dialog = UpgradeDialog(this)
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
                        UpgradeDialog(this).crateDownloadDialog(mission)
                    }
                }
                .subscribeBy()
    }
}