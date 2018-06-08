package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.wexchain.android.common.dp2px
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.dialog.FullScreenDialog
import io.wexchain.android.dcc.view.dialog.FullScreenDialog.Companion.makeWindowFullscreenAndTransparent
import io.wexchain.android.dcc.view.recycler.SpacingDecoration
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanReportBinding
import io.wexchain.dcc.databinding.ItemLoanReportRecordBinding
import io.wexchain.dccchainservice.domain.LoanReport
import java.io.Serializable
import java.util.concurrent.TimeUnit

class LoanReportActivity : BindActivity<ActivityLoanReportBinding>(), ItemViewClickListener<LoanReport> {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_report

    override fun onItemClick(item: LoanReport?, position: Int, viewId: Int) {
        item?.let {
            if(!it.isFromOtherAddress()) {
                navigateTo(LoanReportDetailActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_REPORT, it as Serializable)
                }
            }
        }
    }

    private val adapter = SimpleDataBindAdapter<ItemLoanReportRecordBinding, LoanReport>(
        layoutId = R.layout.item_loan_report_record,
        variableId = BR.report,
        itemViewClickListener = this
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        findViewById<View>(R.id.btn_update_report).setOnClickListener {
            updateReport()
        }
        findViewById<RecyclerView>(R.id.rv_list).run {
            addItemDecoration(SpacingDecoration(context).apply {
                spacingHeight = context.dp2px(16f).toInt()
            })
            adapter = this@LoanReportActivity.adapter
        }
        loadData()
    }

    private fun loadData() {
        binding.idData = CertOperations.getCertIdData()
        CertOperations.reportData?.let {
            setReportData(it)
        }
    }

    private fun setReportData(it: Pair<List<LoanReport>, Long>) {
        binding.reports = it.first
        binding.reportUpdateTime = it.second
    }

    private fun updateReport() {
        ScfOperations
            .withScfTokenInCurrentPassport(emptyList()) {
                App.get().scfApi.queryLoanReport(it)
            }
            .zipWith(Single.timer(4100,TimeUnit.MILLISECONDS), BiFunction<List<LoanReport>,Long,List<LoanReport>> { t1, t2 -> t1 })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                updateDialog.show()
            }
            .doFinally {
                scheduleDismissUpdateDialog()
            }
            .subscribe({
                onNewReportReceived(it)
            }, {})
    }

    private val updateDialog by lazy {
        val dialog = FullScreenDialog(this)
        dialog.setContentView(R.layout.dialog_fullscreen_loading_3)
        val ivg = dialog.findViewById<ImageView>(R.id.iv_gear)
        val anim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        ivg.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                v?.clearAnimation()
            }

            override fun onViewAttachedToWindow(v: View?) {
                v?.startAnimation(anim)
            }
        })
        val txt = dialog.findViewById<TextView>(R.id.tv_loading_text)
        dialog.makeWindowFullscreenAndTransparent{
            txt.text = "正在比对链上信息..."
            txt.postDelayed({
                txt.text = "正在比对借贷报告..."
            },2000)
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog
    }

    private fun scheduleDismissUpdateDialog() {
        val dialog = updateDialog
        dialog.findViewById<TextView>(R.id.tv_loading_text).run {
            text = "比对完成"
            postDelayed({
                dialog.dismiss()
            },500)
        }
    }

    private fun onNewReportReceived(reportList: List<LoanReport>?) {
        reportList?.let{
            val data = it to System.currentTimeMillis()
            CertOperations.reportData = data
            setReportData(data)
        }
    }
}
