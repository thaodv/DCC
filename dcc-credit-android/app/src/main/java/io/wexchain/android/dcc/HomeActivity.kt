package io.wexchain.android.dcc

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xxy.maple.tllibrary.activity.TlBrowserActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.*
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.tl.TlWebPageActivity
import io.wexchain.android.dcc.view.dialog.BonusDialog
import io.wexchain.android.dcc.view.dialog.UpgradeDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityHomeBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CheckUpgrade
import io.wexchain.dccchainservice.domain.RedeemToken
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.ScfAccountInfo
import zlc.season.rxdownload3.core.Mission
import java.io.File

class HomeActivity : BindActivity<ActivityHomeBinding>(), BonusDialog.Listener {

    override val contentLayoutId: Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()

        App.get().passportRepository.currPassport.observe(this, Observer { p ->
            binding.cardPassport.passport = p
        })
        setupClicks()
        checkScfAccount()


    }

    override fun onResume() {
        super.onResume()
        signin()
        checkUpgrade()
    }

    private fun checkUpgrade() {
        App.get().marketingApi.checkUpgrade(getVersionName())
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    it.mandatoryUpgrade
                }
                .subscribe({ it ->
                    showUpgradeDialog(it)
                })
    }

    private fun showUpgradeDialog(it: CheckUpgrade) {
        val dialog = UpgradeDialog(this)
        dialog.createHomeDialog(
                it.versionNumber, it.updateLog,
                onConfirm = {
                    dialog.dismiss()
                    downloadApk(it.versionNumber, it.updateUrl)
                })
    }

    private fun downloadApk(versionNumber: String, updateUrl: String) {
        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                            UpgradeDialog(this).crateDownloadDialog(mission)
                        }
                    } else {
                        toast("没有读写文件权限,请重新打开App授权")
                        finish()
                    }
                }
    }

    private fun checkScfAccount() {
        val passportRepository = App.get().passportRepository
        if (!passportRepository.scfAccountExists) {
            ScfOperations.getScfAccountInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .withLoading()
                    .subscribe { acc ->
                        if (acc === ScfAccountInfo.ABSENT) {
                            atLeastCreated {
                                showRegisterScfWithInviteCodeDialog()
                            }
                        } else {
                            passportRepository.scfAccountExists = true
                        }
                    }
        }
    }

    private fun signin() {

        ScfOperations.loginWithCurrentPassport()
                .subscribe()

        ScfOperations.withScfTokenInCurrentPassport {
            App.get().scfApi.signIn(it)
        }.subscribe({
        }, {
            if (it is DccChainServiceException) {
                print(it.message)
            }
        })
    }

    private val registerScfDialog by lazy { RegisterScfDialog() }

    private fun showRegisterScfWithInviteCodeDialog() {
        registerScfDialog.show()
    }

    private fun setupClicks() {
        val clickDigitalAssets: (View) -> Unit = {
            if (App.get().passportRepository.passportExists) {
                navigateTo(
                        DigitalAssetsActivity::class.java, transitionBundle(
                        Transitions.create(findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
                        , Transitions.create(findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
                )
                )
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<ViewGroup>(R.id.section_assets).apply {
            setOnClickListener(clickDigitalAssets)
            this.findViewById<View>(R.id.btn_detail).setOnClickListener(clickDigitalAssets)
        }
        findViewById<View>(R.id.btn_settings).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                navigateTo(PassportSettingsActivity::class.java)
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<View>(R.id.tv_credit).setOnClickListener {
            if (App.get().passportRepository.passportEnabled) {
                navigateTo(MyCreditActivity::class.java)
            } else {
                if (!App.get().passportRepository.passportExists) {
                    showIntroWalletDialog()
                } else {
                    toast(R.string.ca_not_enabled)
                }
            }
        }
        findViewById<View>(R.id.tv_candy).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                onClickCandy()
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<View>(R.id.tv_loan).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                navigateTo(LoanActivity::class.java)
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<View>(R.id.tv_loan_report).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                navigateTo(LoanReportActivity::class.java)
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<View>(R.id.banner_tl).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                val address = App.get().passportRepository.getCurrentPassport()!!.address
                navigateTo(TlWebPageActivity::class.java) {
                    putExtra(TlBrowserActivity.ADDRESS, address)
                }
            } else {
                showIntroWalletDialog()
            }
        }
        findViewById<View>(R.id.iv_invite).setOnClickListener {
            if (App.get().passportRepository.passportExists) {
                navigateTo(DccAffiliateActivity::class.java)
            } else {
                showIntroWalletDialog()
            }
        }
        binding.cardPassport.root.setOnClickListener {
            if (!App.get().passportRepository.passportExists) {
                showIntroWalletDialog()
            } else {
                navigateTo(
                        PassportActivity::class.java, transitionBundle(
                        Transitions.create(findViewById(R.id.card_passport), Transitions.CARD_PASSPORT),
                        Transitions.create(
                                findViewById<ViewGroup>(R.id.card_passport).findViewById(R.id.iv_avatar),
                                Transitions.CARD_PASSPORT_AVATAR
                        )
                )
                )
            }
        }
    }

    private fun onClickCandy() {
//        //todo delete this mock action
//        navigateTo(DccEcoRewardsActivity::class.java)
//        return
        val passportRepository = App.get().passportRepository
        if (passportRepository.scfAccountExists) {
            ScfOperations
                    .withScfTokenInCurrentPassport(emptyList()) {
                        App.get().scfApi.queryBonus(it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .withLoading()
                    .subscribe { list ->
                        if (list.isEmpty()) {
                            enterMarketingList()
                        } else {
                            val bonus = list.first()
                            showRedeemToken(bonus)
                        }
                    }
        } else {
            enterMarketingList()
        }
    }

    private fun enterMarketingList() {
        navigateTo(MarketingListActivity::class.java)
    }

    private fun showRedeemToken(redeemToken: RedeemToken) {
        BonusDialog.create(redeemToken, this).show(supportFragmentManager, "BONUS#${redeemToken.scenarioCode}")
    }

    override fun onSkip() {
        enterMarketingList()
    }

    override fun onComplete() {
//        enterMarketingList()
    }

    private val introDialog by lazy { IntroDialog() }
    fun showIntroWalletDialog() {
        introDialog.show()
    }

    private fun toCreate() {
        navigateTo(CreatePassportActivity::class.java)
    }

    private fun toImport() {
        navigateTo(PassportImportActivity::class.java)
    }

    private fun registerScfAccount(code: String?) {
        ScfOperations.registerWithCurrentPassport(code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe { _ ->
                    App.get().passportRepository.scfAccountExists = true
                    toast("成功")
                    registerScfDialog.dismiss()
                }
    }

    private inner class IntroDialog : Dialog(this, R.style.FullWidthDialog) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_intro_wallet)

            findViewById<View>(R.id.btn_to_create_wallet).setOnClickListener {
                dismiss()
                toCreate()
            }
            findViewById<View>(R.id.btn_to_import_wallet).setOnClickListener {
                dismiss()
                toImport()
            }
            findViewById<View>(R.id.ib_close).setOnClickListener {
                dismiss()
            }
        }
    }

    private inner class RegisterScfDialog : Dialog(this) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_register_scf)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            findViewById<View>(R.id.btn_confirm).setOnClickListener {
                val code = findViewById<EditText>(R.id.et_input_invite_code).text.toString()
                registerScfAccount(if (code.isEmpty()) null else code)
            }
        }
    }
}
