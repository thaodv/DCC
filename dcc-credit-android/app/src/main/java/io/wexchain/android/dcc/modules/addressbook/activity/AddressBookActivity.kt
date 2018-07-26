package io.wexchain.android.dcc.modules.addressbook.activity

import android.app.ActionBar
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.resultOk
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.CreateTransactionActivity
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.AddressBookAdapter
import io.wexchain.android.dcc.view.addressbook.FloatingBarItemDecoration
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.DigitalCurrency
import java.io.Serializable
import java.util.*

class AddressBookActivity : BaseCompatActivity(), ItemViewClickListener<AddressBook> {

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency?

    private var mHeaderList: LinkedHashMap<Int, String>? = null
    private var mContactList: ArrayList<AddressBook>? = null

    private var mRecyclerView: RecyclerView? = null

    private var mOperationInfoDialog: PopupWindow? = null
    private var mLetterHintView: View? = null
    private var mLlEmpty: LinearLayout? = null
    private var mFloatingBarItemDecoration: FloatingBarItemDecoration? = null

    private var mUsage: Int = 0

    private val passportRepository = App.get().passportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses_book)
        initToolbar()

        mUsage = intent.getIntExtra("usage", 0)

        title = if (0 == mUsage) {
            getString(R.string.address_book)
        } else {
            getString(R.string.select_address)
        }

        mRecyclerView = findViewById(R.id.rv_list)
        mLlEmpty = findViewById(R.id.ll_empty)

        mRecyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mFloatingBarItemDecoration = FloatingBarItemDecoration(this)
        mRecyclerView!!.removeItemDecoration(mFloatingBarItemDecoration)
        mRecyclerView!!.addItemDecoration(mFloatingBarItemDecoration)

    }

    private fun getDataFormDataBase() {
        passportRepository.addressBooks.observe(this, Observer {

            mContactList = it as ArrayList<AddressBook>?

            if (mContactList!!.isEmpty()) {
                mRecyclerView!!.visibility = View.GONE
                mLlEmpty!!.visibility = View.VISIBLE

            } else {

                if (mHeaderList == null) {
                    mHeaderList = LinkedHashMap()
                }

                mRecyclerView!!.visibility = View.VISIBLE
                mLlEmpty!!.visibility = View.GONE

                mContactList!!.sortWith(Comparator { l, r -> l.compareTo(r) })

                mHeaderList!!.clear()
                if (mContactList!!.size == 0) {
                    return@Observer
                }
                addHeaderToList(0, mContactList!![0].initial)
                for (i in 1 until mContactList!!.size) {
                    if (!mContactList!![i - 1].initial.equals(mContactList!![i].initial, ignoreCase = true)) {
                        addHeaderToList(i, mContactList!![i].initial)
                    }
                }

                mFloatingBarItemDecoration!!.setList(mHeaderList)

                val adapter = AddressBookAdapter(this)
                adapter.setList(mContactList)
                mRecyclerView!!.adapter = adapter

            }
        })
    }

    override fun onResume() {
        super.onResume()
        getDataFormDataBase()

        if (null != mFloatingBarItemDecoration) {
            mFloatingBarItemDecoration!!.setList(mHeaderList)
        }
    }

    private fun addHeaderToList(index: Int, header: String) {
        mHeaderList!![index] = header
    }

    private fun showLetterHintDialog(s: String) {
        if (mOperationInfoDialog == null) {
            mLetterHintView = layoutInflater.inflate(R.layout.dialog_letter_hint, null)
            mOperationInfoDialog = PopupWindow(mLetterHintView, ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, false)
            mOperationInfoDialog!!.isOutsideTouchable = true
        }
        (mLetterHintView!!.findViewById(R.id.dialog_letter_hint_textview) as TextView).text = s
        window.decorView.post {
            mOperationInfoDialog!!.showAtLocation(window.decorView.findViewById(android.R.id
                    .content), Gravity.CENTER, 0, 0)
        }
    }

    private fun hideLetterHintDialog() {
        mOperationInfoDialog!!.dismiss()
    }

    override fun onItemClick(item: AddressBook?, position: Int, viewId: Int) {
        item?.let { ba ->
            if (0 == mUsage) {
                toDetail(ba)
            } else if (1 == mUsage) {
                resultOk {
                    putExtra(Extras.EXTRA_SELECT_ADDRESS, ba)
                }
            } else if (2 == mUsage) {
                startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc).putExtra(Extras.EXTRA_SELECT_ADDRESS, ba)
                })
                finish()
            }
        }
    }

    private fun toDetail(ba: AddressBook) {
        navigateTo(AddressDetailActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_address_book, menu)

        menu!!.findItem(R.id.address_book_add).isVisible = (0 == mUsage)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.address_book_query -> {
                if (mUsage == 0) {
                    navigateTo(AddressBookQueryActivity::class.java) {
                        putExtra("usage", mUsage)
                    }
                } else {
                    navigateTo(AddressBookQueryActivity::class.java) {
                        putExtra("usage", mUsage)
                                .putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
                    }
                }


                true
            }
            R.id.address_book_add -> {
                navigateTo(AddAddressBookActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
