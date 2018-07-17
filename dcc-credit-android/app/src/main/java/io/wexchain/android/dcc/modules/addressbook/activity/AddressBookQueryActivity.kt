package io.wexchain.android.dcc.modules.addressbook.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.CreateTransactionActivity
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.addressbook.vm.QueryBookAddressVm
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.repo.db.QueryHistory
import io.wexchain.android.dcc.view.ClearEditText
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BookAddressQueryAdapter
import io.wexchain.android.dcc.view.adapters.BookAddressQueryHistoryAdapter
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.DigitalCurrency
import java.io.Serializable

class AddressBookQueryActivity : BaseCompatActivity(), TextWatcher, ItemViewClickListener<BeneficiaryAddress> {


    private var mRecyclerView: RecyclerView? = null
    private var mRvHistory: RecyclerView? = null
    private var mIbtDelete: ImageButton? = null
    private var mEtSearch: EditText? = null
    private var mBtSerrch: Button? = null

    private var mEmpty: LinearLayout? = null

    private var mAdapter: BookAddressQueryAdapter? = null

    private var mHistoryAdapter: BookAddressQueryHistoryAdapter? = null

    private var queryBookAddressVm: QueryBookAddressVm? = null

    private var mUsage: Int = 0

    private var dc: DigitalCurrency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book_query)

        mUsage = intent.getIntExtra("usage", 0)

        if (mUsage != 0) {
            dc = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency?
        }

        findViewById<ImageButton>(R.id.iv_back).setOnClickListener { finish() }

        mEtSearch = findViewById<ClearEditText>(R.id.et_search)
        mRecyclerView = findViewById(R.id.rv_list)
        mRvHistory = findViewById(R.id.rv_history)
        mIbtDelete = findViewById(R.id.ibt_delete)
        mEmpty = findViewById(R.id.ll_empty)
        mBtSerrch = findViewById(R.id.bt_search)

        mEtSearch!!.addTextChangedListener(this)

        queryBookAddressVm = getViewModel()

        mAdapter = BookAddressQueryAdapter(this)
        mAdapter!!.lifecycleOwner = this

        mRecyclerView!!.adapter = mAdapter

        mHistoryAdapter = BookAddressQueryHistoryAdapter(object : ItemViewClickListener<QueryHistory> {
            override fun onItemClick(item: QueryHistory?, position: Int, viewId: Int) {
                mEtSearch!!.setText(item!!.name)
            }
        })

        mHistoryAdapter!!.lifecycleOwner = this

        mRvHistory!!.layoutManager = GridLayoutManager(this, 3)
        mRvHistory!!.adapter = mHistoryAdapter

        queryBookAddressVm!!.getHistory().observe(this, Observer {
            if (it!!.isEmpty()) {
                mRvHistory!!.visibility = View.GONE
                mRecyclerView!!.visibility = View.GONE
                mEmpty!!.visibility = View.GONE
                mIbtDelete!!.visibility = View.GONE
            } else {
                mRecyclerView!!.visibility = View.GONE
                mEmpty!!.visibility = View.GONE
                mRvHistory!!.visibility = View.VISIBLE
                mIbtDelete!!.visibility = View.VISIBLE
                mHistoryAdapter!!.setList(it)
            }
        })

        mIbtDelete!!.setOnClickListener {
            App.get().passportRepository.deleteAddressBookQueryHistory()
        }

        mBtSerrch!!.setOnClickListener {
            val name = mEtSearch!!.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                toast("请输入搜索关键词")
            } else {
                App.get().passportRepository.addOrReplaceQueryHistory(QueryHistory(name, SystemClock.currentThreadTimeMillis()))
                mEtSearch!!.text.clear()
            }
        }

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var text = s.toString().trim()
        if (text.isEmpty()) {
            mRecyclerView!!.visibility = View.GONE
            mEmpty!!.visibility = View.GONE
        } else {
            mRvHistory!!.visibility = View.GONE
            mRecyclerView!!.visibility = View.VISIBLE
            queryBookAddressVm!!.getRes("%$text%").observe(this, Observer {
                if (it!!.isEmpty()) {
                    mEmpty!!.visibility = View.VISIBLE
                    mRecyclerView!!.visibility = View.GONE
                } else {
                    mEmpty!!.visibility = View.GONE
                    mAdapter!!.setList(it)
                }
            })
        }
    }


    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
        item?.let { ba ->
            if (0 == mUsage) {
                toDetail(ba)
            } else {
                startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc).putExtra(Extras.EXTRA_SELECT_ADDRESS, ba)
                })
            }
        }
    }

    private fun toDetail(ba: BeneficiaryAddress) {
        navigateTo(AddressDetailActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

}
