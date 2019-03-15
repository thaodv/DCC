package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.TrustTradeDetailStyleSelectDialog
import io.wexchain.android.dcc.view.dialog.TrustTradeDetailTimeSelectDialog
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTradeDetailBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.trustpocket.QueryDepositOrderPageBean

class TrustTradeDetailActivity : BindActivity<ActivityTrustTradeDetailBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_trade_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

    }

    class TradeDetailVm : PagedVm<QueryDepositOrderPageBean>() {
        override fun loadPage(page: Int): Single<PagedList<QueryDepositOrderPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryDepositOrderPage(it, "", page, 20).check()
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
                        /*vm!!.startTime = DateUtil.getCurrentMonday()
                        vm!!.endTime = DateUtil.getCurrentSunday()

                        vm!!.refresh {}
                        binding.tvTip.text = resources.getString(R.string.this_week)
                        startTime = DateUtil.getCurrentMonday()
                        endTime = DateUtil.getCurrentSunday()
                        queryExchangeAmount()*/
                    }

                    override fun month() {
                        /*vm!!.startTime = DateUtil.getMinMonthDate()
                        vm!!.endTime = DateUtil.getMaxMonthDate()

                        vm!!.refresh {}
                        binding.tvTip.text = resources.getString(R.string.this_month)
                        startTime = DateUtil.getMinMonthDate()
                        endTime = DateUtil.getMaxMonthDate()
                        queryExchangeAmount()*/
                    }

                    override fun startTime() {

                        /*val datePickerDialog = DatePickerDialog(this@AcrossTransRecordActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            mYear = year
                            mMonth = month
                            mDay = dayOfMonth

                            if (month >= 9) {
                                if (mDay >= 10) {
                                    accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                                } else {
                                    accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/0").append(mDay).toString()
                                }
                            } else {
                                if (mDay >= 10) {
                                    accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
                                } else {
                                    accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/0").append(mDay).toString()
                                }
                            }


                        }, mYear, mMonth, mDay)

                        val datePicker = datePickerDialog.datePicker
                        datePicker.minDate = DateUtil.getLongTime("2018/07/20", "yyyy/MM/dd")
                        datePicker.maxDate = System.currentTimeMillis()
                        datePickerDialog.show()*/

                    }

                    override fun endTime() {
                        /*val datePickerDialog = DatePickerDialog(this@AcrossTransRecordActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            mYear = year
                            mMonth = month
                            mDay = dayOfMonth

                            if (month >= 9) {

                                if (mDay >= 10) {
                                    accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                                } else {
                                    accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/0").append(mDay).toString()
                                }
                            } else {
                                if (mDay >= 10) {
                                    accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
                                } else {
                                    accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/0").append(mDay).toString()
                                }
                            }

                        }, mYear, mMonth, mDay)

                        val datePicker = datePickerDialog.datePicker
                        datePicker.minDate = DateUtil.getLongTime("2018/07/20", "yyyy/MM/dd")
                        datePicker.maxDate = System.currentTimeMillis()
                        datePickerDialog.show()*/
                    }

                    override fun sure() {

                        /*if (DateUtil.isRightSelect(accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", ""), accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {
                            toast("结束时间不能小于起始时间")
                            return
                        }

                        if (DateUtil.beyond90day(accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", ""), accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {

                            vm!!.startTime = accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", "")
                            vm!!.endTime = accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", "")

                            vm!!.refresh {}

                            binding.tvTip.text = accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", "") + "~" + accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", "")
                            startTime = accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", "")
                            endTime = accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", "")
                            queryExchangeAmount()

                        } else {
                            toast("日期跨度不能超过3个月")
                        }*/
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

                    override fun cancel() {
                    }


                })
                trustTradeDetailStyleSelectDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
