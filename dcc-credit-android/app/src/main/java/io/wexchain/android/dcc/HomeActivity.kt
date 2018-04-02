package io.wexchain.android.dcc

import android.app.Dialog
import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.common.transitionBundle
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.auth.R

class HomeActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()

        setContentView(R.layout.activity_home)
        findViewById<View>(R.id.card_my_credit)?.setOnClickListener {
            navigateTo(MyCreditActivity::class.java)
        }
        findViewById<View>(R.id.card_digital_assets)?.setOnClickListener {
            clickDigitalAssets()
        }
    }

    fun clickDigitalAssets() {
        if (App.get().passportRepository.passportExists) {
            navigateTo(DigitalAssetsActivity::class.java, transitionBundle(
                    Transitions.create(findViewById(R.id.rv_assets), Transitions.DIGITAL_ASSETS_LIST)
                    , Transitions.create(findViewById(R.id.assets_amount_label), Transitions.DIGITAL_ASSETS_AMOUNT_LABEL)
                    , Transitions.create(findViewById(R.id.assets_amount_value), Transitions.DIGITAL_ASSETS_AMOUNT)
            ))
        } else {
            showIntroWalletDialog()
        }
    }

    private val introDialog by lazy { IntroDialog() }
    private fun showIntroWalletDialog() {
        introDialog.show()
    }

    private fun toCreate() {
        navigateTo(CreatePassportActivity::class.java)
    }

    private fun toImport() {
        navigateTo(ImportPassportActivity::class.java)
    }

    private inner class IntroDialog :Dialog(this){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_intro_wallet)
            findViewById<View>(R.id.btn_create_passport).setOnClickListener {
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
