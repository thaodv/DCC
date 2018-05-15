package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import com.wexmarket.android.passport.ResultCodes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.*
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.dialog.ConfirmLoanSubmitDialog
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.vm.StartLoanVm
import io.wexchain.android.dcc.vm.domain.LoanScratch
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityStartLoanBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.LoanChainOrder
import io.wexchain.dccchainservice.domain.LoanProduct
import io.wexchain.dccchainservice.domain.LoanStatus

class StartLoanActivity : BindActivity<ActivityStartLoanBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_start_loan

    private val product
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_PRODUCT) as? LoanProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val p = product
        if (p == null) {
            postOnMainThread {
                //todo
                finish()
            }
        } else {
            initData(p)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.vm?.apply {
            completedCert.set(CertOperations.getCompletedCerts())
            loadHolding()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_BENEFICIARY_ADDRESS -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    val vm = binding.vm
                    val ba = data?.getSerializableExtra(Extras.EXTRA_BENEFICIARY_ADDRESS) as? BeneficiaryAddress
                    if (vm != null && ba != null) {
                        vm.address.set(ba)
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initData(p: LoanProduct) {
        val vm = getViewModel<StartLoanVm>().apply {
            setProduct(p)
        }
        vm.proceedEvent.observe(this, Observer {
            it?.let {
                ConfirmLoanSubmitDialog.create(it, this@StartLoanActivity::checkOrderAndProceed)
                    .show(supportFragmentManager, "confirm_loan")
            }
        })
        vm.failEvent.observe(this, Observer {
            it?.let { toast(it) }
        })
        vm.refreshEvent.observe(this, Observer {
            binding.executePendingBindings()
        })
        vm.volSelChangedEvent.observe(this, Observer {
            it?.let {
                if (it != -1) {
                    binding.etInputVol.hideIme()
                    binding.etInputVol.clearFocus()
                }else{
                    binding.etInputVol.requestFocus()
                }
            }
        })
        binding.vm = vm
        App.get().passportRepository.getDefaultBeneficiaryAddress()
            .subscribe { ba ->
                binding.vm?.let {
                    if (it.address.get() == null) {
                        it.address.set(ba)
                    }
                }
            }
        binding.btnProceed.setOnClickListener {
            binding.vm?.checkAndProceed()
        }
        binding.isbFee.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.vm?.feeAdditional?.set(progress)
                binding.executePendingBindings()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.tvBeneficiaryAddress.setOnClickListener {
            startActivityForResult(
                Intent(this, ChooseBeneficiaryAddressActivity::class.java),
                RequestCodes.CHOOSE_BENEFICIARY_ADDRESS
            )
        }
        binding.tvRequisiteValue.setOnClickListener {
            product?.let {
                navigateTo(RequisiteCertListActivity::class.java) {
                    putExtra(Extras.EXTRA_REQUISITE_CERTS, it.requisiteCertList.toTypedArray())
                }
            }
        }
        binding.tvAgreementNoticeLink.setOnClickListener {
            product?.agreementTemplateUrl?.let {
                navigateTo(ViewPdfActivity::class.java){
                    putExtra(Extras.EXTRA_PDF_URL,it)
                }
            }
        }
    }

    private fun checkOrderAndProceed(loanScratch: LoanScratch) {
        val scfApi = App.get().scfApi
        ScfOperations
            .withScfTokenInCurrentPassport(LoanChainOrder.ABSENT_ORDER) {
                scfApi.getLastOrder(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                if (it.isNextOrderRestricted()) {
                    showCancelPrevOrderDialog(it)
                } else {
                    doSubmitLoan(loanScratch)
                }
            }
    }

    private fun doSubmitLoan(loanScratch: LoanScratch) {
        val passport = App.get().passportRepository.getCurrentPassport()
        passport ?: return
        ScfOperations.submitLoan(loanScratch, passport)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe({ _ ->
                toast("提交贷款申请成功")
                navigateTo(LoanSubmitResultActivity::class.java)
            },{
                if (it is DccChainServiceException) {
                    Pop.toast(it.message?:"系统错误",this)
                }
            })
    }

    private fun showCancelPrevOrderDialog(it: LoanChainOrder) {
        if (it.status == LoanStatus.AUDITING) {
            CustomDialog(this).apply {
                textContent = "存在审核中订单,无法再次申请借款"
                withPositiveButton()
            }.assembleAndShow()
        } else if (it.status == LoanStatus.CREATED) {
            CustomDialog(this).apply {
                textContent = "订单可能已经超时，建议您取消订单后重新申请，借币申请手续费将原路退还"
                withPositiveButton("取消订单") {
                    doCancelOrder(it)
                    true
                }
                withNegativeButton("继续等待")
            }.assembleAndShow()
        }
    }

    private fun doCancelOrder(order: LoanChainOrder) {
        ScfOperations.cancelLoan(order.id)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe({ _ ->
                toast("取消成功")
            },{
                if (it is DccChainServiceException) {
                    Pop.toast(it.message?:"系统错误",this)
                }
            })
    }
}
