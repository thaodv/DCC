package io.wexchain.android.dcc.modules.addressbook.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.CreateTransactionActivity
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.addressbook.vm.QueryBookAddressVm
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.ClearEditText
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BookAddressQueryAdapter
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.DigitalCurrency
import java.io.Serializable

class AddressBookQueryActivity : BaseCompatActivity(), TextWatcher, ItemViewClickListener<AddressBook> {

    private var mRecyclerView: RecyclerView? = null
    private var mEtSearch: EditText? = null

    private var mEmpty: LinearLayout? = null

    private var mAdapter: BookAddressQueryAdapter? = null

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
        mEmpty = findViewById(R.id.ll_empty)

        mEtSearch!!.addTextChangedListener(this)

        queryBookAddressVm = getViewModel()

        mAdapter = BookAddressQueryAdapter(this)
        mAdapter!!.lifecycleOwner = this

        mRecyclerView!!.adapter = mAdapter

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var text = s.toString().trim()
        mEtSearch!!.setSelection(text.length)
        if (text.isEmpty()) {
            mRecyclerView!!.visibility = View.GONE
            mEmpty!!.visibility = View.GONE
        } else {
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

    override fun onItemClick(item: AddressBook?, position: Int, viewId: Int) {
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

    private fun toDetail(ba: AddressBook) {
        navigateTo(AddressDetailActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

}
