package io.wexchain.android.dcc.modules.trans.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.DigitalCurrencyActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.tools.onNoDoubleClick
import io.wexchain.android.dcc.view.dialog.WaitTransDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccExchangeBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransaction

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

        binding.ibDccToPublic.onNoDoubleClick {
            val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
            navigateTo(Private2PublicActivity::class.java) {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccJuzix)
            }
        }


        binding.ibPublicToDcc.onNoDoubleClick {
            val dccPublic = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
            if (dccPublic.chain == Chain.publicEthChain) {//公链

                val eth = SharedPreferenceUtil.get(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction

                if (null == eth) {
                    navigateTo(Public2PrivateActivity::class.java) {
                        putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccPublic)
                    }
                } else {
                    val p = App.get().passportRepository.getCurrentPassport()!!

                    var agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccPublic)

                    agent.getNonce(p.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                        if (it > eth.nonce) {
                            navigateTo(Public2PrivateActivity::class.java) {
                                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccPublic)
                            }
                        } else {
                            val waitTransDialog = WaitTransDialog(this@DccExchangeActivity)
                            waitTransDialog.mTvText.text = "请待「待上链」交易变为「已上链」后再提交新的交易。"
                            waitTransDialog.show()
                        }
                    }, {})
                }
            } else {
                navigateTo(Public2PrivateActivity::class.java) {
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccPublic)
                }
            }
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
