package io.wexchain.android.dcc

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.trans.activity.AcrossTransRecordActivity
import io.wexchain.android.dcc.modules.trans.activity.Private2PublicActivity
import io.wexchain.android.dcc.modules.trans.activity.Public2PrivateActivity
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccExchangeBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies

class DccExchangeActivity : BindActivity<ActivityDccExchangeBinding>() {

    override val contentLayoutId: Int = R.layout.activity_dcc_exchange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()
        binding.cardPublic.root.setOnClickListener {
            val dccPublic = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
            navigateTo(DigitalCurrencyActivity::class.java) {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccPublic)
                putExtra(Extras.EXTRA_DC_SELECTED, true)
            }
        }
        binding.cardJuzix.root.setOnClickListener {
            val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
            navigateTo(DigitalCurrencyActivity::class.java) {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccJuzix)
                putExtra(Extras.EXTRA_DC_SELECTED, true)
            }
        }
        binding.ibDccToPublic.setOnClickListener {
            navigateTo(Private2PublicActivity::class.java)
        }
        binding.ibPublicToDcc.setOnClickListener {
            navigateTo(Public2PrivateActivity::class.java)
        }
        val sp = getSharedPreferences("setting", Context.MODE_PRIVATE)
        if (sp.getBoolean("first_into", true)) {
            sp.edit().putBoolean("first_into", false).commit()
            tipsDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_exchange, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_question -> {
                tipsDialog.show()
                true
            }
            R.id.action_trans_record -> {
                navigateTo(AcrossTransRecordActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val tipsDialog by lazy { TipsDialog() }

    private inner class TipsDialog : Dialog(this, R.style.FullWidthDialog) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_dcc_exchange_tips)
            setCancelable(false)
            window.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
            window.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
            setCanceledOnTouchOutside(false)
            findViewById<View>(R.id.btn_ok).setOnClickListener {
                dismiss()
            }
        }
    }
}
