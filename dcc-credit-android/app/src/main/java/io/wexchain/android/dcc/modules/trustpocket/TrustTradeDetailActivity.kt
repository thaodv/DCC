package io.wexchain.android.dcc.modules.trustpocket

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.TrustTradeDetailStyleSelectDialog
import io.wexchain.android.dcc.view.dialog.TrustTradeDetailTimeSelectDialog
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTradeDetailBinding
import io.wexchain.dcc.databinding.ItemTrustRechargeRecordBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.trustpocket.QueryDepositOrderPageBean
import io.wexchain.dccchainservice.util.DateUtil

class TrustTradeDetailActivity : BindActivity<ActivityTrustTradeDetailBinding>(), ItemViewClickListener<QueryDepositOrderPageBean> {

    override fun onItemClick(item: QueryDepositOrderPageBean?, position: Int, viewId: Int) {
        navigateTo(TrustRechargeDetailActivity::class.java) {
            putExtra("id", item!!.id)
        }
    }

    override val contentLayoutId: Int get() = R.layout.activity_trust_trade_detail

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    private val adapter = SimpleDataBindAdapter<ItemTrustRechargeRecordBinding, QueryDepositOrderPageBean>(
            layoutId = R.layout.item_trust_recharge_record,
            variableId = BR.bean,
            itemViewClickListener = this@TrustTradeDetailActivity
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        val vm = getViewModel<TradeDetailVm>()
        val srl = binding.srlList

        srl.setOnRefreshListener { sr ->
            vm.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm.loadNext { sr.finishLoadMore() }
        }

        binding.rvList.adapter = adapter
        binding.vm = vm

    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    class TradeDetailVm : PagedVm<QueryDepositOrderPageBean>() {
        override fun loadPage(page: Int): Single<PagedList<QueryDepositOrderPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryDepositOrderPage(it, "ETH", page, 20).check()
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
                trustTradeDetailTimeSelectDialog.setOnClickListener(object : TrustTradeDetailTimeSelectDialog.OnClickListener {

                    override fun week() {
                    }

                    override fun month() {
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
                        datePicker.minDate = DateUtil.getLongTime("2018/07/20", "yyyy/MM/dd")
                        datePicker.maxDate = System.currentTimeMillis()
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
                        datePicker.minDate = DateUtil.getLongTime("2018/07/20", "yyyy/MM/dd")
                        datePicker.maxDate = System.currentTimeMillis()
                        datePickerDialog.show()
                    }

                    override fun sure() {

                        if (DateUtil.isRightSelect(trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", ""), trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {
                            toast("结束时间不能小于起始时间")
                            return
                        }

                        if (DateUtil.beyond90day(trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", ""), trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {

                            /*vm!!.startTime = trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", "")
                            vm!!.endTime = trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", "")

                            vm!!.refresh {}

                            binding.tvTip.text = trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", "") + "~" + trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", "")
                            startTime = trustTradeDetailTimeSelectDialog.mTvStartTime.text.toString().replace("/", "")
                            endTime = trustTradeDetailTimeSelectDialog.mTvEndTime.text.toString().replace("/", "")
                            queryExchangeAmount()*/

                        } else {
                            toast("日期跨度不能超过3个月")
                        }
                    }

                })
                trustTradeDetailTimeSelectDialog.show()
                true
            }
            R.id.choose -> {
                val trustTradeDetailStyleSelectDialog = TrustTradeDetailStyleSelectDialog(this)
                trustTradeDetailStyleSelectDialog.setOnClickListener(object : TrustTradeDetailStyleSelectDialog.OnClickListener {
                    override fun in1() {

                    }

                    override fun out() {
                    }

                    override fun myin() {
                    }

                    override fun myout() {
                    }
                })
                trustTradeDetailStyleSelectDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
