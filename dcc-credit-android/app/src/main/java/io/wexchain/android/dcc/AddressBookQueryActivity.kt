package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.repo.db.QueryHistory
import io.wexchain.android.dcc.view.ClearEditText
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BookAddressQueryAdapter
import io.wexchain.android.dcc.view.adapters.BookAddressQueryHistoryAdapter
import io.wexchain.android.dcc.vm.QueryBookAddressVm
import io.wexchain.dcc.R
import java.io.Serializable

class AddressBookQueryActivity : BaseCompatActivity(), TextWatcher, View.OnKeyListener, ItemViewClickListener<BeneficiaryAddress> {

    private var mRecyclerView: RecyclerView? = null
    private var mRvHistory: RecyclerView? = null
    private var mIbtDelete: ImageButton? = null
    private var mEtSearch: EditText? = null

    private var mEmpty: LinearLayout? = null

    private var mAdapter: BookAddressQueryAdapter? = null

    private var mHistoryAdapter: BookAddressQueryHistoryAdapter? = null

    private var queryBookAddressVm: QueryBookAddressVm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book_query)

        findViewById<ImageButton>(R.id.iv_back).setOnClickListener { finish() }

        mEtSearch = findViewById<ClearEditText>(R.id.et_search)
        mRecyclerView = findViewById(R.id.rv_list)
        mRvHistory = findViewById(R.id.rv_history)
        mIbtDelete = findViewById(R.id.ibt_delete)
        mEmpty = findViewById(R.id.ll_empty)

        mEtSearch!!.addTextChangedListener(this)
        mEtSearch!!.setOnKeyListener(this)

        queryBookAddressVm = getViewModel()

        mAdapter = BookAddressQueryAdapter(this)
        mAdapter!!.lifecycleOwner = this

        mRecyclerView!!.adapter = mAdapter

        mHistoryAdapter = BookAddressQueryHistoryAdapter()
        mHistoryAdapter!!.lifecycleOwner = this

        mRvHistory!!.layoutManager = GridLayoutManager(this, 3)
        mRvHistory!!.adapter = mHistoryAdapter

        queryBookAddressVm!!.getHistory().observe(this, Observer {
            if (it!!.isEmpty()) {
                mRvHistory!!.visibility = View.GONE
                mRecyclerView!!.visibility = View.GONE
                mEmpty!!.visibility = View.GONE
            } else {
                mRecyclerView!!.visibility = View.GONE
                mEmpty!!.visibility = View.GONE
                mRvHistory!!.visibility = View.VISIBLE
                mHistoryAdapter!!.setList(it)
            }
        })

        mIbtDelete!!.setOnClickListener {
            App.get().passportRepository.deleteAddressBookQueryHistory()
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

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event!!.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
            // 先隐藏键盘
            ((getSystemService(Context.INPUT_METHOD_SERVICE)) as InputMethodManager)!!.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存

            val name = mEtSearch!!.text.toString().trim()

            App.get().passportRepository.addOrReplaceQueryHistory(QueryHistory(name,SystemClock.currentThreadTimeMillis()))

            mEtSearch!!.text.clear()


        }
        return false
    }

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
        item?.let { ba ->
            toDetail(ba)
        }
    }

    private fun toDetail(ba: BeneficiaryAddress) {
        navigateTo(AddressDetailActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }


}
