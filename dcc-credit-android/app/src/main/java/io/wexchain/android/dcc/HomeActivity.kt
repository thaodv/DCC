package io.wexchain.android.dcc

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.common.toast
import io.wexchain.android.common.transitionBundle
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityHomeBinding

class HomeActivity : BindActivity<ActivityHomeBinding>() {
    override val contentLayoutId: Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()

        App.get().passportRepository.currPassport.observe(this, Observer {p->
            binding.cardPassport.passport = p
        })
        setupClicks()
    }

    private fun setupClicks() {
        val clickDigitalAssets: (View) -> Unit = {
            if (App.get().passportRepository.passportExists) {
                navigateTo(DigitalAssetsActivity::class.java, transitionBundle(
                        Transitions.create(findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
                        , Transitions.create(findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
                ))
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
                navigateTo(MarketingListActivity::class.java)
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
        binding.cardPassport.root.setOnClickListener {
            if (!App.get().passportRepository.passportExists) {
                showIntroWalletDialog()
            }else{
                navigateTo(PassportActivity::class.java,transitionBundle(
                        Transitions.create(findViewById(R.id.card_passport),Transitions.CARD_PASSPORT),
                        Transitions.create(findViewById<ViewGroup>(R.id.card_passport).findViewById(R.id.iv_avatar),Transitions.CARD_PASSPORT_AVATAR)
                ))
            }
        }
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

    private inner class IntroDialog :Dialog(this,R.style.FullWidthDialog){
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
}
