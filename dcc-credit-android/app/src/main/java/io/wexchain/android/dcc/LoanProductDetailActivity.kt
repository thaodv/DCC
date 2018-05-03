package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanProductDetailBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.BusinessCodes
import io.wexchain.dccchainservice.domain.LoanProduct
import io.wexchain.dccchainservice.domain.Result
import java.io.Serializable

class LoanProductDetailActivity : BindActivity<ActivityLoanProductDetailBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_product_detail

    private val loanProductId
        get() = intent.getLongExtra(Extras.EXTRA_LOAN_PRODUCT_ID,-1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        loadData()
        initClicks()
    }

    private fun initClicks() {
        binding.btnStartLoan.setOnClickListener {
            (binding.product as? Serializable)?.let {
                navigateTo(StartLoanActivity::class.java){
                    putExtra(Extras.EXTRA_LOAN_PRODUCT,it)
                }
            }
        }
    }

    private fun loadData() {
        val id = loanProductId
        if (id == -1L){
            postOnMainThread {
                toast("借款产品id无效")
                finish()
            }
        }else{
            App.get().scfApi.queryLoanProductById(id)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateProductInfo(it)
                },{
                    val e = it.cause?:it
                    if(e is DccChainServiceException && e.businessCode == BusinessCodes.LOAN_PRODUCT_NOT_EXIST){
                        toast("借款产品id无效/不存在")
                    }
                    finish()
                })
        }
    }

    private fun updateProductInfo(product: LoanProduct) {
        title = product.description
        binding.product = product
    }
}
