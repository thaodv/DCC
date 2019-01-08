package io.wexchain.android.dcc.modules.home

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.commitTransaction
import io.wexchain.android.common.installApk
import io.wexchain.android.common.toast
import io.wexchain.android.common.versionInfo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.fragment.home.DigitalAssetsFragment
import io.wexchain.android.dcc.fragment.home.FindFragment
import io.wexchain.android.dcc.fragment.home.MineFragment
import io.wexchain.android.dcc.fragment.home.ServiceFragment
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.tools.reName
import io.wexchain.android.dcc.view.bottomnavigation.BottomNavigationBar
import io.wexchain.android.dcc.view.bottomnavigation.BottomNavigationItem
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.android.dcc.view.dialog.BonusDialog
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.CheckUpgrade
import io.wexchain.dccchainservice.domain.RedeemToken
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import zlc.season.rxdownload3.core.Mission
import java.io.File


/**
 *Created by liuyang on 2018/9/18.
 */
class HomeActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initView()
        initEvent()
        initPhototTask()

        showRedeem()
    }

    private fun showRedeem() {
        ScfOperations
                .withScfTokenInCurrentPassport(emptyList()) {
                    App.get().scfApi.queryBonus(it)
                }
                .filter {
                    it.isNotEmpty()
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    val bonus = it.first()
                    showRedeemToken(bonus)
                }
    }

    override fun onResume() {
        super.onResume()
        checkUpgrade()
    }

    private fun showRedeemToken(redeemToken: RedeemToken) {
        BonusDialog.create(redeemToken, object : BonusDialog.Listener {
            override fun onSkip() {

            }

            override fun onComplete() {

            }
        }).show(supportFragmentManager, "BONUS#${redeemToken.scenarioCode}")
    }

    private fun initEvent() {
        bottom_navigation_bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabSelected(position: Int) {
                when (position) {
                    0 -> replaceFragment(ServiceFragment())
                    1 -> replaceFragment(FindFragment.getInstance())
                    2 -> replaceFragment(DigitalAssetsFragment())
                    3 -> replaceFragment(MineFragment())
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commitTransaction {
            replace(R.id.layFrame, fragment)
        }
    }

    private fun initView() {
        bottom_navigation_bar.apply {
            setMode(BottomNavigationBar.MODE_FIXED)
            setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            addItem(BottomNavigationItem(R.drawable.tab_activity_service, "服务").setInactiveIconResource(R.drawable.tab_service).setActiveColorResource(R.color.main_tab))
            addItem(BottomNavigationItem(R.drawable.tab_activity_find, "发现").setInactiveIconResource(R.drawable.tab_find).setActiveColorResource(R.color.main_tab))
            addItem(BottomNavigationItem(R.drawable.tab_activity_digital, "钱包").setInactiveIconResource(R.drawable.tab_digital).setActiveColorResource(R.color.main_tab))
            addItem(BottomNavigationItem(R.drawable.tab_activity_mine, "我的").setInactiveIconResource(R.drawable.tab_mine).setActiveColorResource(R.color.main_tab))
            setFirstSelectedPosition(1)
            initialise()
        }
        replaceFragment(FindFragment.getInstance(intent.getStringExtra("data")))
    }

    private fun initPhototTask() {
        doAsync {
            val certIdPics = CertOperations.getTmpIdIdPics()
            certIdPics?.let {
                if (it.first.exists()) {
                    it.first.reName("positivePhoto.jpg")
                }
                if (it.second.exists()) {
                    it.second.reName("backPhoto.jpg")
                }
                if (it.third.exists()) {
                    it.third.reName("facePhoto.jpg")
                }
            }
        }
    }


    private fun checkUpgrade() {
        App.get().marketingApi.checkUpgrade(versionInfo.versionCode.toString())
                .checkonMain()
                .subscribeBy(
                        onSuccess = {
                            if (it.mandatoryUpgrade) {
                                showUpgradeDialog(it)
                            } else {
                                val oldVersionName = ShareUtils.getString(Extras.SP_VERSION_NAME, "")

                                if (it.version != oldVersionName) {
                                    showUpgradeDialog(it)
                                }
                            }
                        }, onError = {})
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
                                ShareUtils.setString(Extras.SP_VERSION_NAME, it.version)
                            },
                            onConfirm = {
                                dialog.dismiss()
                                downloadApk(it.version, it.updateUrl)
                            })
        }
    }

    private fun downloadApk(versionNumber: String, updateUrl: String) {
        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
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
                    } else {
                        toast("没有读写文件权限,请重新打开App授权")
                        finish()
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.get().passportRepository.setUserInfo("")
    }


}
