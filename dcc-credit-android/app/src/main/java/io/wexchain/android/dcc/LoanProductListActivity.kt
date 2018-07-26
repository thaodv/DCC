package io.wexchain.android.dcc

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.*
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemLoanProductBinding
import io.wexchain.dccchainservice.domain.LoanProduct

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
        startActivity(Intent.createChooser(intent, "发送邮件"))
    }

    private val adapter = Adapter(this::onItemClick)
    private val insertFixItemAdapter = InsertFixItemAdapter(
            adapter,
            object : InsertFixItemAdapter.InsertFixViewProvider {
                override fun inflateBottomView(parent: ViewGroup): View {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.header_loan_provider_banner, parent, false)
                    return view
                }

                override fun onBind(bottomView: View?, position: Int) {
                }

                override val insertionPos: Int = 0

            }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_product_list)
        initToolbar()
        adapter.lifecycleOwner = this
        findViewById<RecyclerView>(R.id.rv_list).adapter = BottomMoreItemsAdapter(insertFixItemAdapter, this)
        loadData()
    }

    override fun onBackPressed() {
        finishAndGotoLoan()
    }

    override fun handleHomePressed(): Boolean {
        finishAndGotoLoan()
        return true
    }

    private fun finishAndGotoLoan() {
        ActivityCompat.finishAfterTransition(this)
        navigateTo(LoanActivity::class.java) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    fun onItemClick(position: Int, viewId: Int) {
        val pos = insertFixItemAdapter.getOriginalPos(position)
        onItemClick(adapter.getItemOnPos(pos), pos, viewId)
    }

    override fun onItemClick(item: LoanProduct?, position: Int, viewId: Int) {
        item?.let {
            navigateTo(LoanProductDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_PRODUCT_ID, it.id)
            }
        }
    }

    private fun loadData() {
        App.get().scfApi.queryLoanProductsByLenderCode()
                .checkonMain()
                .subscribeBy {
                    adapter.setList(it)
                    findViewById<RecyclerView>(R.id.rv_list).scrollToPosition(0)
                }
    }


    private class Adapter(val onPosClick: (Int, Int) -> Unit) :
            DataBindAdapter<ItemLoanProductBinding, LoanProduct>(
                    R.layout.item_loan_product,
                    itemDiffCallback = defaultItemDiffCallback()
            ) {
        override fun bindData(binding: ItemLoanProductBinding, item: LoanProduct?) {
            binding.product = item
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemLoanProductBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemLoanProductBinding = DataBindingUtil.inflate(layoutInflater, layout, parent, false)
            return ClickAwareHolder(binding, onPosClick)
        }
    }
}
