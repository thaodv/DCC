package io.wexchain.android.dcc.modules.trans.activity

import android.app.DatePickerDialog
import android.os.Bundle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.trans.vm.AcrossTransRecordsVm
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.AccrossTransRecordSelectDialog
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAcrossTransRecordBinding
import io.wexchain.dcc.databinding.ItemTransRecordBinding
import io.wexchain.dccchainservice.domain.AccrossTransRecord
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.dccchainservice.util.DateUtil.getLongTime


class AcrossTransRecordActivity : BindActivity<ActivityAcrossTransRecordBinding>(), ItemViewClickListener<AccrossTransRecord> {

    internal var mYear: Int = 0
    internal var mMonth: Int = 0
    internal var mDay: Int = 0

    var vm: AcrossTransRecordsVm? = null

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
                }

                override fun month() {
                    vm!!.startTime = DateUtil.getMinMonthDate()
                    vm!!.endTime = DateUtil.getMaxMonthDate()

                    vm!!.refresh {}
                }

                override fun latest2month() {
                    vm!!.startTime = DateUtil.getPre2Month()
                    vm!!.endTime = DateUtil.getCurrentDate()

                    vm!!.refresh {}
                }

                override fun startTime() {

                    val datePickerDialog = DatePickerDialog(this@AcrossTransRecordActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth

                        if (month >= 9) {
                            accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                        } else {
                            accrossTransRecordSelectDialog.mTvStartTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
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
                            accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/").append(mMonth + 1).append("/").append(mDay).toString()
                        } else {
                            accrossTransRecordSelectDialog.mTvEndTime.text = StringBuffer().append(mYear).append("/0").append(mMonth + 1).append("/").append(mDay).toString()
                        }

                    }, mYear, mMonth, mDay)

                    val datePicker = datePickerDialog.datePicker
                    datePicker.minDate = getLongTime("2018/07/20", "yyyy/MM/dd")
                    datePicker.maxDate = System.currentTimeMillis()
                    datePickerDialog.show()
                }

                override fun sure() {

                    vm!!.startTime = accrossTransRecordSelectDialog.mTvStartTime.text.toString().replace("/", "")
                    vm!!.endTime = accrossTransRecordSelectDialog.mTvEndTime.text.toString().replace("/", "")

                    vm!!.refresh {}
                }

            })
            accrossTransRecordSelectDialog.show()
        }


    }

    private fun queryExchangeAmount() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.queryExchangeAmount(it).observeOn(AndroidSchedulers.mainThread())

                }.subscribe({ binding.tvContent.text = "转到公链 " + it.toPublicAmount + "DCC  转到私链" + it.toPrivateAmount + "DCC" }, {})
    }

    override fun onItemClick(item: AccrossTransRecord?, position: Int, viewId: Int) {

    }


}
