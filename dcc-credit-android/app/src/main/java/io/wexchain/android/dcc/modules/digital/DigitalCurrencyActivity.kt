package io.wexchain.android.dcc.modules.digital

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.passport.PassportAddressActivity
import io.wexchain.android.dcc.modules.trans.activity.SelectTransStyleActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustRechargeActivity
import io.wexchain.android.dcc.tools.NoDoubleClickListener
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.TransactionRecordAdapter
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.view.dialog.WaitTransDialog
import io.wexchain.android.dcc.vm.BottomMoreVm
import io.wexchain.android.dcc.vm.DigitalCurrencyVm
import io.wexchain.android.dcc.vm.TransactionListVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalCurrencyBinding
import io.wexchain.dcc.databinding.ItemBottomMoreTextBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.ipfs.utils.doMain

/**
 * Digital currency detail
 * holding / quote / transactions
 */
class DigitalCurrencyActivity : BindActivity<ActivityDigitalCurrencyBinding>(), ItemViewClickListener<EthsTransaction> {

    override val contentLayoutId: Int = R.layout.activity_digital_currency

    var isOpenTrustPocket: Boolean = false
    var isSupportTrust: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        initVm()
        initBtn()

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 已开户
                    if (it?.mobileUserId != null) {
                        isOpenTrustPocket = true
                        App.get().mobileUserId = it.mobileUserId

                    } else {
                        isOpenTrustPocket = false
                    }
                }, {
                    // 未开户
                    isOpenTrustPocket = false
                })

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getDepositWallet(it, getDc().symbol).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    isSupportTrust = true
                }, {
                    isSupportTrust = false
                })

    }

    override fun onItemClick(item: EthsTransaction?, position: Int, viewId: Int) {
        item ?: return
        startActivityForResult(Intent(this, DigitalTransactionDetailActivity::class.java).apply {
            putExtra(Extras.EXTRA_DIGITAL_TRANSACTION, item)
        }, RequestCodes.CANCLE_TRANSACTION)
    }

    private fun initVm() {
        if (getDc().chain == Chain.publicEthChain) {//公链
            val eth = SharedPreferenceUtil.get(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction

            if (null == eth) {
                //toCreateTransaction()
            } else {
                val p = App.get().passportRepository.getCurrentPassport()!!
                var agent = App.get().assetsRepository.getDigitalCurrencyAgent(getDc())
                agent.getNonce(p.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    if (it > eth.nonce) {
                        // toCreateTransaction()
                        App.get().assetsRepository.removePendingTx(eth.txId, eth.nonce)
                    } else {
                        App.get().assetsRepository.pushPendingTx(eth)
                    }
                }, {})
            }
        }

        val dc = getDc()
        val address = App.get().passportRepository.currPassport.value!!.address
        val vm = ViewModelProviders.of(this).get(address, DigitalCurrencyVm::class.java).apply {
            ensure(address, dc, this@DigitalCurrencyActivity)
        }
        vm.fetchDataFailEvent.observe(this, Observer {
            toast("网络异常,请稍后重试")
        })
        vm.confirmEvent.observe(this, Observer {
            it ?: return@Observer
            val dialog = DeleteAddressBookDialog(this)
            dialog.setTvText(it.first.toString())
            dialog.setBtnText("确认", "取消")
            dialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                override fun cancel() {
                    dialog.dismiss()
                }

                override fun sure() {
                    dialog.dismiss()
                    it.second(true)
                }
            })
            dialog.show()
        })
        vm.selectedChangedEvent.observe(this, Observer {
            it?.let {
                if (it) {
                    toast(getString(R.string.success))
                } else {
                    toast(getString(R.string.deleted))
                }
            }
        })
        binding.vm = vm
        val txListVm = getViewModel<TransactionListVm>("${dc.contractAddress}:$address").apply {
            ensure(dc, address, false)
        }
        txListVm.viewMoreEvent.observe(this, Observer {
            toTransactionListActivity()
        })
        txListVm.pendingTxDoneEvent.observe(this, Observer {
            vm.load()
        })
        binding.txList = txListVm
        val adapter = TransactionRecordAdapter(address, this)
        val bm = BottomMoreVm().apply {
            noMoreHint.set(getText(R.string.no_more))
            loadMoreHint.set(getText(R.string.load_more))
        }
        val bottomMoreItemsAdapter = BottomMoreItemsAdapter(adapter, object : BottomMoreItemsAdapter.BottomViewProvider {

            override fun inflateBottomView(parent: ViewGroup): View {
                parent
                val b = DataBindingUtil.inflate<ItemBottomMoreTextBinding>(LayoutInflater.from(parent.context), R.layout.item_bottom_more_text, parent, false)
                b.bm = bm
                b.tvLoadMore.setOnClickListener {
                    toTransactionListActivity()
                }
                return b.root
            }

            override fun onBind(bottomView: View?, position: Int) {
            }
        })
        binding.sectionTransactions!!.rvTransactions.adapter = bottomMoreItemsAdapter
        txListVm.list.observe(this, Observer {
            //  Log.e("sssssssssssssss", "txListVm " + it?.size)
            val list3 = it?.subList(0, minOf(it.size, 3)) ?: emptyList()
            adapter.setList(list3)
            txListVm.empty.set(list3.isEmpty())
            bm.hasMore.set(it != null && it.size > 3)

        })
    }

    private fun toTransactionListActivity() {
        startActivity(Intent(this, DigitalTransactionsActivity::class.java).apply {
            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, getDc())
        })
    }

    private fun getDc() =
            intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY)!! as DigitalCurrency

    private fun initBtn() {

        binding.btnToTransfer.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (getDc().chain == Chain.publicEthChain) {//公链

                    val eth = SharedPreferenceUtil.get(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction

                    if (null == eth) {
                        toCreateTransaction()
                    } else {
                        val p = App.get().passportRepository.getCurrentPassport()!!

                        var agent = App.get().assetsRepository.getDigitalCurrencyAgent(getDc())

                        agent.getNonce(p.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it > eth.nonce) {
                                toCreateTransaction()
                            } else {
                                val waitTransDialog = WaitTransDialog(this@DigitalCurrencyActivity)
                                waitTransDialog.mTvText.text = "请待「待上链」交易变为「已上链」后再提交新的交易。"
                                waitTransDialog.show()
                            }
                        }, {})
                    }
                } else {
                    toCreateTransaction()
                }
            }
        })

        binding.btnToCollect.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                startActivity(Intent(this@DigitalCurrencyActivity, PassportAddressActivity::class.java))
            }
        })

        binding.btTrust.onClick {
            if (isOpenTrustPocket) {

                if (isSupportTrust) {
                    navigateTo(TrustRechargeActivity::class.java) {
                        putExtra("code", getDc().symbol)
                        putExtra("url", if (null == getDc().icon) "" else getDc().icon)
                    }
                } else {
                    toast("托管钱包暂不支持该币种")
                }
            } else {
                navigateTo(TrustPocketOpenTipActivity::class.java)
            }
        }

    }

    private fun toCreateTransaction() {
        val dc = getDc()
        if (dc.chain == Chain.JUZIX_PRIVATE && dc.symbol == Currencies.FTC.symbol) {
        } else {
            startActivityForResult(Intent(this, SelectTransStyleActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
            }, RequestCodes.CREATE_TRANSACTION)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.vm!!.load()
        binding.txList?.let {
            it.showAll.set(false)
        }
    }
}
