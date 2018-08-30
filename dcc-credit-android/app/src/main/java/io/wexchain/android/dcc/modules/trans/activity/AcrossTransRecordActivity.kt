package io.wexchain.android.dcc.modules.trans.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.trans.vm.AcrossTransRecordsVm
import io.wexchain.android.dcc.tools.StringUtils
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.AccrossTransRecordSelectDialog
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAcrossTransRecordBinding
import io.wexchain.dcc.databinding.ItemTransRecordBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.AccrossTransRecord
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.dccchainservice.util.DateUtil.getLongTime
import io.wexchain.ipfs.utils.doMain


class AcrossTransRecordActivity : BindActivity<ActivityAcrossTransRecordBinding>(), ItemViewClickListener<AccrossTransRecord> {

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    var vm: AcrossTransRecordsVm? = null

    var startTime: String = DateUtil.getCurrentMonday()

    var endTime: String = DateUtil.getCurrentSunday()

    override val contentLayoutId: Int
        get() = R.layout.activity_across_trans_record

    private val adapter = SimpleDataBindAdapter<ItemTransRecordBinding, AccrossTransRecord>(
            layoutId = R.layout.item_trans_record,
            variableId = BR.order,
            itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        queryExchangeAmount()

        binding.rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.rvList.adapter = adapter
        vm = getViewModel()

        vm!!.startTime = DateUtil.getCurrentMonday()
        vm!!.endTime = DateUtil.getCurrentSunday()

        vm!!.refresh {}

        binding.srlList.setOnRefreshListener { srl ->
            vm!!.refresh { srl.finishRefresh() }
        }
        binding.srlList.setOnLoadMoreListener { srl ->
            vm!!.loadNext { srl.finishLoadMore() }
        }
        binding.srlList.setRefreshHeader(ClassicsHeader(this))
        binding.srlList.setRefreshFooter(ClassicsFooter(this))
        binding.records = vm

        binding.ivSelectTime.setOnClickListener {
            val accrossTransRecordSelectDialog = AccrossTransRecordSelectDialog(this)
            accrossTransRecordSelectDialog.setOnClickListener(object : AccrossTransRecordSelectDialog.OnClickListener {

                override fun week() {
                    vm!!.startTime = DateUtil.getCurrentMonday()
                    vm!!.endTime = DateUtil.getCurrentSunday()

                    vm!!.refresh {}
                    binding.tvTip.text = resources.getString(R.string.this_week)
                    startTime = DateUtil.getCurrentMonday()
                    endTime = DateUtil.getCurrentSunday()
                    queryExchangeAmount()
                }

                override fun month() {
                    vm!!.startTime = DateUtil.getMinMonthDate()
                    vm!!.endTime = DateUtil.getMaxMonthDate()

                    vm!!.refresh {}
                    binding.tvTip.text = resources.getString(R.string.this_month)
                    startTime = DateUtil.getMinMonthDate()
                    endTime = DateUtil.getMaxMonthDate()
                    queryExchangeAmount()
                }

                override fun latest2month() {
                    vm!!.startTime = DateUtil.getPre2Month()
                    vm!!.endTime = DateUtil.getCurrentDate()

                    vm!!.refresh {}
                    binding.tvTip.text = resources.getString(R.string.latest_2_month)
                    startTime = DateUtil.getPre2Month()
                    endTime = DateUtil.getCurrentDate()
                    queryExchangeAmount()
                }

                override fun startTime() {

                    val datePickerDialog = DatePickerDialog(this@AcrossTransRecordActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
                    datePicker.minDate = getLongTime("2018/07/20", "yyyy/MM/dd")
                    datePicker.maxDate = System.currentTimeMillis()
                    datePickerDialog.show()

                }

                override fun endTime() {
                    val datePickerDialog = DatePickerDialog(this@AcrossTransRecordActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
                    datePicker.minDate = getLongTime("2018/07/20", "yyyy/MM/dd")
                    datePicker.maxDate = System.currentTimeMillis()
                    datePickerDialog.show()
                }

                override fun sure() {

                    if (DateUtil.isRightSelect(accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", ""), accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", ""), "yyyyMMdd")) {
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
                    }
                }

            })
            accrossTransRecordSelectDialog.show()
        }
    }

    private fun queryExchangeAmount() {
        ScfOperations
                .withScfTokenInCurrentPassport(emptyList()) {
                    App.get().scfApi.queryExchangeAmount(it, startTime = startTime, endTime = endTime)
                }
                .doMain()
                .subscribe({
                    var public = "0"
                    var private = "0"

                    if (null != it) {
                        for (items in it) {
                            if (items.isPublic2Private()) {
                                public = ViewModelHelper.getTransCount(items.totalAmount)
                            } else {
                                private = ViewModelHelper.getTransCount(items.totalAmount)
                            }
                        }
                        binding.tvContent.text = "转到公链 " + StringUtils.getTransCount(private, 2) + " 转到私链" + StringUtils.getTransCount(public, 2)
                    }

                }, {
                    if (it is DccChainServiceException) {
                        binding.tvContent.text = "转到公链 0 转到私链 0"
                    } else {
                        binding.tvContent.text = "xcccc"
                    }
                })
    }

    override fun onItemClick(item: AccrossTransRecord?, position: Int, viewId: Int) {
        item?.let { ba ->

            if (item.isPublic2Private()) {
                startActivity(Intent(this, TransPublic2PrivateDetailActivity::class.java).putExtra("id", item.id))
            } else {
                startActivity(Intent(this, TransPrivate2PublicDetailActivity::class.java).putExtra("id", item.id))
            }
        }
    }
}


