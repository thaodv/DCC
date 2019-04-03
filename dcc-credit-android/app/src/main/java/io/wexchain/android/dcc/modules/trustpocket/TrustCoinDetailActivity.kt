package io.wexchain.android.dcc.modules.trustpocket

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.reactivex.Single
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustTradeDetailStyleSelectDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustTradeDetailTimeSelectDialog
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustCoinDetailBinding
import io.wexchain.dcc.databinding.ItemTrustTradeDetailBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.trustpocket.GetBalanceLogBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain
import java.math.RoundingMode
import java.text.SimpleDateFormat

class TrustCoinDetailActivity : BindActivity<ActivityTrustCoinDetailBinding>(), ItemViewClickListener<GetBalanceLogBean> {

    val mUrl get() = intent.getStringExtra("url")
    val mCode get() = intent.getStringExtra("code")
    val mName get() = intent.getStringExtra("name")
    val mValue get() = intent.getStringExtra("value")
    val mValue2 get() = intent.getStringExtra("value2")

    override fun onItemClick(item: GetBalanceLogBean?, position: Int, viewId: Int) {

        /*if ("DEPOSIT" == item!!.kind) {
            navigateTo(TrustRechargeDetailActivity::class.java) {
                putExtra("id", item!!.id)
            }
        } else if ("WITHDRAW" == item.kind) {
            navigateTo(TrustWithdrawDetailActivity::class.java) {
                putExtra("id", item!!.requestIdentity.requestNo)
            }
        } else if ("TRANSFER-IN" == item.kind) {
            navigateTo(TrustTransferDetailActivity::class.java) {
                putExtra("id", item!!.requestIdentity.requestNo)
                putExtra("type", "1")
            }
        } else {
            navigateTo(TrustTransferDetailActivity::class.java) {
                putExtra("id", item!!.requestIdentity.requestNo)
                putExtra("type", "2")
            }
        }*/
    }

    override val contentLayoutId: Int get() = R.layout.activity_trust_coin_detail

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    var vm: TradeDetailVm? = null

    var mStartTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd"))
    var mEndTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
    var mType: String = "DEPOSIT"

    private val adapter = SimpleDataBindAdapter<ItemTrustTradeDetailBinding, GetBalanceLogBean>(
            layoutId = R.layout.item_trust_coin_detail,
            variableId = BR.bean,
            itemViewClickListener = this@TrustCoinDetailActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }

        binding.url = mUrl
        binding.name = mName
        binding.assetCode = mCode
        binding.holding = mValue
        binding.holdingValue = mValue2


        vm = getViewModel()
        val srl = binding.srlList

        srl.setOnRefreshListener { sr ->
            vm!!.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm!!.loadNext { sr.finishLoadMore() }
        }

        vm!!.checkData.observe(this, Observer {
            val status = binding.llEmpty.visibility
            if (status != it!!) {
                binding.llEmpty.visibility = it
            }
        })

        binding.rvList.adapter = adapter
        binding.vm = vm

        vm!!.assCode = mCode

        binding.llIn.onClick {
            navigateTo(TrustRechargeActivity::class.java) {
                putExtra("code", mCode)
                putExtra("url", mUrl)
            }
        }

        binding.llGet.onClick {
            navigateTo(TrustGetmoneyActivity::class.java) {
                putExtra("code", mCode)
                putExtra("url", mUrl)
            }
        }

        binding.llOut.onClick {
            navigateTo(TrustWithdrawActivity::class.java) {
                putExtra("code", mCode)
                putExtra("url", mUrl)
            }
        }

        binding.llTrans.onClick {
            navigateTo(TrustTransferCheckActivity::class.java)
        }

        binding.tvAll.onClick {

            binding.tvAll.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vAllTip.visibility = View.VISIBLE
            binding.tvIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vInTip.visibility = View.INVISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vOutTip.visibility = View.INVISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "ALL"
            mType = "ALL"

            binding.srlList.autoRefresh()

        }

        binding.tvIn.onClick {

            binding.tvAll.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vAllTip.visibility = View.INVISIBLE
            binding.tvIn.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vInTip.visibility = View.VISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vOutTip.visibility = View.INVISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "IN"
            mType = "IN"

            binding.srlList.autoRefresh()

        }

        binding.tvOut.onClick {

            binding.tvAll.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vAllTip.visibility = View.INVISIBLE
            binding.tvIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vInTip.visibility = View.INVISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vOutTip.visibility = View.VISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "OUT"
            mType = "OUT"

            binding.srlList.autoRefresh()

        }

        /*binding.tvMyOut.onClick {

            binding.tvIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vInTip.visibility = View.INVISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vOutTip.visibility = View.INVISIBLE
            binding.tvMyIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyInTip.visibility = View.INVISIBLE
            binding.tvMyOut.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vMyOutTip.visibility = View.VISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "TRANSFER-OUT"
            mType = "TRANSFER-OUT"

            binding.srlList.autoRefresh()

        }*/

        binding.ibtTime.onClick {
            val trustTradeDetailTimeSelectDialog = TrustTradeDetailTimeSelectDialog(this)

            trustTradeDetailTimeSelectDialog.mTvStartTime.text = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd"))
            trustTradeDetailTimeSelectDialog.mTvEndTime.text = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))

            trustTradeDetailTimeSelectDialog.setOnClickListener(object : TrustTradeDetailTimeSelectDialog.OnClickListener {

                override fun week() {
                    vm!!.startTimeV = DateUtil.getCurrentMonday(SimpleDateFormat("yyyy/MM/dd"))
                    mStartTime = DateUtil.getCurrentMonday(SimpleDateFormat("yyyy/MM/dd"))
                    vm!!.endTimeV = DateUtil.getCurrentSunday(SimpleDateFormat("yyyy/MM/dd"))
                    mEndTime = DateUtil.getCurrentSunday(SimpleDateFormat("yyyy/MM/dd"))
                    vm!!.typeV = mType
                    binding.srlList.autoRefresh()
                }

                override fun month() {
                    vm!!.startTimeV = DateUtil.getMinMonthDate(SimpleDateFormat("yyyy/MM/dd"))
                    mStartTime = DateUtil.getMinMonthDate(SimpleDateFormat("yyyy/MM/dd"))
                    vm!!.endTimeV = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
                    mEndTime = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
                    vm!!.typeV = mType
                    binding.srlList.autoRefresh()
                }

                override fun startTime() {


                    val datePickerDialog = DatePickerDialog(this@TrustCoinDetailActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth

                        if (month >= 9) {
                            if (mDay >= 10) {
                                trustTradeDetailTimeSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                            } else {
                                trustTradeDetailTimeSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/0").append(mDay).toString()
                            }
                        } else {
                            if (mDay >= 10) {
                                trustTradeDetailTimeSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
                            } else {
                                trustTradeDetailTimeSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/0").append(mDay).toString()
                            }
                        }


                    }, mYear, mMonth, mDay)

                    val datePicker = datePickerDialog.datePicker
                    datePicker.minDate = DateUtil.getLongTime(DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")), "yyyy/MM/dd")
                    datePicker.maxDate = DateUtil.getLongTime(DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd")), "yyyy/MM/dd")

                    datePickerDialog.show()

                }

                override fun endTime() {
                    val datePickerDialog = DatePickerDialog(this@TrustCoinDetailActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth

                        if (month >= 9) {

                            if (mDay >= 10) {
                                trustTradeDetailTimeSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                            } else {
                                trustTradeDetailTimeSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/0").append(mDay).toString()
                            }
                        } else {
                            if (mDay >= 10) {
                                trustTradeDetailTimeSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
                            } else {
                                trustTradeDetailTimeSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/0").append(mDay).toString()
                            }
                        }

                    }, mYear, mMonth, mDay)

                    val datePicker = datePickerDialog.datePicker
                    datePicker.minDate = DateUtil.getLongTime(DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd")), "yyyy/MM/dd")
                    datePicker.maxDate = DateUtil.getLongTime(DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd")), "yyyy/MM/dd")
                    datePickerDialog.show()
                }

                override fun sure() {

                    if (DateUtil.isRightSelect(trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", ""), trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {
                        toast("结束时间不能小于起始时间")
                        return
                    }

                    if (DateUtil.beyond90day(trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", ""), trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {

                        vm!!.startTimeV = trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString()
                        mStartTime = trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString()
                        vm!!.endTimeV = trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString()
                        mEndTime = trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString()

                        binding.srlList.autoRefresh()

                    } else {
                        toast("日期跨度不能超过1个月")
                    }
                }

            })
            trustTradeDetailTimeSelectDialog.show()
        }

        binding.ibtDetail.onClick {
            val trustTradeDetailStyleSelectDialog = TrustTradeDetailStyleSelectDialog(this)
            trustTradeDetailStyleSelectDialog.setOnClickListener(object : TrustTradeDetailStyleSelectDialog.OnClickListener {
                override fun in1() {
                    vm!!.startTimeV = mStartTime
                    vm!!.endTimeV = mEndTime
                    vm!!.typeV = "DEPOSIT"
                    mType = "DEPOSIT"
                    vm!!.refresh {}
                }

                override fun out() {
                    vm!!.startTimeV = mStartTime
                    vm!!.endTimeV = mEndTime
                    vm!!.typeV = "WITHDRAW"
                    mType = "WITHDRAW"
                    vm!!.refresh {}
                }

                override fun myin() {
                    vm!!.startTimeV = mStartTime
                    vm!!.endTimeV = mEndTime
                    vm!!.typeV = "TRANSFER-IN"
                    mType = "TRANSFER-IN"
                    vm!!.refresh {}
                }

                override fun myout() {
                    vm!!.startTimeV = mStartTime
                    vm!!.endTimeV = mEndTime
                    vm!!.typeV = "TRANSFER-OUT"
                    mType = "TRANSFER-OUT"
                    vm!!.refresh {}
                }
            })
            trustTradeDetailStyleSelectDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
        getBalance(mCode)
    }

    private fun getBalance(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .subscribe({

                    binding.holding = it.availableAmount.assetValue.amount.toBigDecimal().setSelfScale(8)
                    binding.holdingValue = "≈￥" + it.availableAmount.legalTenderPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).setScale(2, RoundingMode.DOWN).toPlainString()

                }, {
                    toast(it.message.toString())
                })
    }

    class TradeDetailVm : PagedVm<GetBalanceLogBean>() {

        var startTimeV: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd"))
        var endTimeV: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
        var typeV: String = "ALL"
        var assCode: String = "ETH"

        override fun loadPage(page: Int): Single<PagedList<GetBalanceLogBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.getBalanceLog(it, assCode, page, 20, startTime = startTimeV, endTime = endTimeV, type = typeV).check()
            }
        }
    }
}
