package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.Single
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustTradeDetailTimeSelectDialog
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTradeDetailBinding
import io.wexchain.dcc.databinding.ItemTrustTradeDetailBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.trustpocket.QueryOrderPageBean
import io.wexchain.dccchainservice.util.DateUtil
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class TrustTradeDetailActivity : BindActivity<ActivityTrustTradeDetailBinding>(), ItemViewClickListener<QueryOrderPageBean> {

    override fun onItemClick(item: QueryOrderPageBean?, position: Int, viewId: Int) {

        if ("DEPOSIT" == item!!.kind) {
            navigateTo(TrustRechargeDetailActivity::class.java) {
                putExtra("id", item!!.id)
            }
        } else if ("WITHDRAW" == item.kind) {
            navigateTo(TrustWithdrawDetailActivity::class.java) {
                putExtra("id", item!!.requestIdentity.requestNo)
            }
        } else if ("TRANSFER-IN" == item.kind) {

            if("TRANSFER" == item.type){
                navigateTo(TrustTransferDetailActivity::class.java) {
                    putExtra("id", item!!.requestIdentity.requestNo)
                    putExtra("type", "1")
                }
            }else{
                navigateTo(TrustTransferGetDetailActivity::class.java) {
                    putExtra("id", item!!.requestIdentity.requestNo)
                    putExtra("type", "1")
                }
            }
        } else {
            if("TRANSFER" == item.type){
                navigateTo(TrustTransferDetailActivity::class.java) {
                    putExtra("id", item!!.requestIdentity.requestNo)
                    putExtra("type", "2")
                }
            }else{
                navigateTo(TrustTransferGetDetailActivity::class.java) {
                    putExtra("id", item!!.requestIdentity.requestNo)
                    putExtra("type", "2")
                }
            }
        }
    }

    override val contentLayoutId: Int get() = R.layout.activity_trust_trade_detail

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    var vm: TradeDetailVm? = null

    var mStartTime: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd"))
    var mEndTime: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
    var mType: String = "DEPOSIT"

    private val adapter = SimpleDataBindAdapter<ItemTrustTradeDetailBinding, QueryOrderPageBean>(
            layoutId = R.layout.item_trust_trade_detail,
            variableId = BR.bean,
            itemViewClickListener = this@TrustTradeDetailActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

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

        binding.tvIn.onClick {

            binding.tvIn.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vInTip.visibility = View.VISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vOutTip.visibility = View.INVISIBLE
            binding.tvMyIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyInTip.visibility = View.INVISIBLE
            binding.tvMyOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyOutTip.visibility = View.INVISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "DEPOSIT"
            mType = "DEPOSIT"

            binding.srlList.autoRefresh()

        }

        binding.tvOut.onClick {

            binding.tvIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vInTip.visibility = View.INVISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vOutTip.visibility = View.VISIBLE
            binding.tvMyIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyInTip.visibility = View.INVISIBLE
            binding.tvMyOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyOutTip.visibility = View.INVISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "WITHDRAW"
            mType = "WITHDRAW"

            binding.srlList.autoRefresh()

        }

        binding.tvMyIn.onClick {

            binding.tvIn.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vInTip.visibility = View.INVISIBLE
            binding.tvOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vOutTip.visibility = View.INVISIBLE
            binding.tvMyIn.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vMyInTip.visibility = View.VISIBLE
            binding.tvMyOut.setTextColor(resources.getColor(R.color.FF9B9B9B))
            binding.vMyOutTip.visibility = View.INVISIBLE

            vm!!.startTimeV = mStartTime
            vm!!.endTimeV = mEndTime
            vm!!.typeV = "TRANSFER-IN"
            mType = "TRANSFER-IN"

            binding.srlList.autoRefresh()

        }

        binding.tvMyOut.onClick {

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

        }

    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    class TradeDetailVm : PagedVm<QueryOrderPageBean>() {

        var startTimeV: String = DateUtil.getPre1Month(SimpleDateFormat("yyyy/MM/dd"))
        var endTimeV: String = DateUtil.getCurrentDate(SimpleDateFormat("yyyy/MM/dd"))
        var typeV: String = "DEPOSIT"

        override fun loadPage(page: Int): Single<PagedList<QueryOrderPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryOrderPage(it, "", page, 20, startTime = startTimeV, endTime = endTimeV, type = typeV).checkonMain()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_trust_trade_detail_choose, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.time -> {

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

                        val datePickerDialog = DatePickerDialog(this@TrustTradeDetailActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
                        val datePickerDialog = DatePickerDialog(this@TrustTradeDetailActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
