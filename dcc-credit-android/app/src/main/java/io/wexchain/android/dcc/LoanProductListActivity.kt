package io.wexchain.android.dcc

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.network.GlideApp
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemLoanProductBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import io.wexchain.dccchainservice.domain.Result

class LoanProductListActivity : BaseCompatActivity(), ItemViewClickListener<LoanProduct>,
    BottomMoreItemsAdapter.BottomViewProvider {
    override fun inflateBottomView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_business_contact_2, parent, false)
        view.setOnClickListener {
            toBusinessEmail()
        }
        return view
    }

    override fun onBind(bottomView: View?, position: Int) {
    }


    private fun toBusinessEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.bitexpress_info_mail_address)))
        startActivity(Intent.createChooser(intent,"发送邮件"))
    }

    private val adapter = SimpleDataBindAdapter<ItemLoanProductBinding, LoanProduct>(
        layoutId = R.layout.item_loan_product,
        variableId = BR.product,
        itemViewClickListener = this@LoanProductListActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_product_list)
        initToolbar()
        adapter.lifecycleOwner = this
        findViewById<RecyclerView>(R.id.rv_list).adapter = BottomMoreItemsAdapter(adapter,this)
        loadData()
    }

    override fun onItemClick(item: LoanProduct?, position: Int, viewId: Int) {
        item?.let {
            navigateTo(LoanProductDetailActivity::class.java){
                putExtra(Extras.EXTRA_LOAN_PRODUCT_ID,it.id)
            }
        }
    }

    private fun loadData() {
        App.get().scfApi.queryLoanProductsByLenderCode()
            .compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                adapter.setList(it)
                if(it.isNotEmpty()){
                    val logoUrl = it.first().lender.logoUrl
                    val ivLogo = findViewById<ImageView>(R.id.iv_provider_logo)
                    GlideApp.with(ivLogo)
                        .load(logoUrl)
                        .into(ivLogo)
                }
            }
    }
}
