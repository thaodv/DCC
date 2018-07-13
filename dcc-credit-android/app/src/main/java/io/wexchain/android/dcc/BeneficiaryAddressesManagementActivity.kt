package io.wexchain.android.dcc

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
import android.widget.PopupWindow
import android.widget.TextView
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.resultOk
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BeneficiaryAddressAdapter
import io.wexchain.android.dcc.view.addressbook.FloatingBarItemDecoration
import io.wexchain.android.dcc.view.addressbook.IndexBar
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.DigitalCurrency
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class BeneficiaryAddressesManagementActivity : BaseCompatActivity(), ItemViewClickListener<BeneficiaryAddress> {

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

    private var mHeaderList: LinkedHashMap<Int, String>? = null
    private var mContactList: ArrayList<BeneficiaryAddress>? = null

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = LinearLayoutManager(this)

    private var mOperationInfoDialog: PopupWindow? = null
    private var mLetterHintView: View? = null
    private var mIndexBar: IndexBar? = null
    private var mFloatingBarItemDecoration: FloatingBarItemDecoration? = null

    private var mUsage: Int = 0

    private val passportRepository = App.get().passportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beneficiary_addresses_management)
        initToolbar()

        mUsage = intent.getIntExtra("usage", 0)

        if (0 == mUsage) {
            title = "地址簿"
        } else {
            title = "选择地址"
        }

        mRecyclerView = findViewById(R.id.rv_list)
        mIndexBar = findViewById(R.id.share_add_contact_sidebar)

        mRecyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        mFloatingBarItemDecoration = FloatingBarItemDecoration(this)

        mRecyclerView!!.removeItemDecoration(mFloatingBarItemDecoration)
        mRecyclerView!!.addItemDecoration(mFloatingBarItemDecoration)

    }

    private fun getDataFormDataBase() {
        passportRepository.beneficiaryAddresses.observe(this, Observer {

            if (mHeaderList == null) {
                mHeaderList = LinkedHashMap()
            }

            mContactList = it as ArrayList<BeneficiaryAddress>?
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

            val adapter = BeneficiaryAddressAdapter(this)
            adapter.setList(mContactList)
            mRecyclerView!!.adapter = adapter

            mIndexBar!!.setNavigators(ArrayList(mHeaderList!!.values))
            mIndexBar!!.setOnTouchingLetterChangedListener(object : IndexBar.OnTouchingLetterChangeListener {
                override fun onTouchingLetterChanged(s: String) {
                    showLetterHintDialog(s)
                    for (position in mHeaderList!!.keys) {
                        if (mHeaderList!![position] == s) {
                            mLayoutManager!!.scrollToPositionWithOffset(position, 0)
                            return
                        }
                    }
                }

                override fun onTouchingStart(s: String) {
                    showLetterHintDialog(s)
                }

                override fun onTouchingEnd(s: String) {
                    hideLetterHintDialog()
                }
            })
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

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
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
            }
        }
    }

    private fun toDetail(ba: BeneficiaryAddress) {
        navigateTo(AddressDetailActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

    private fun toEdit(ba: BeneficiaryAddress) {
        navigateTo(EditBeneficiaryAddressActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

    private fun confirmDelete(ba: BeneficiaryAddress) {
        CustomDialog(this).apply {
            setTitle("确认删除以下信息")
            textContent = "${ba.shortName}\n${ba.address}"
        }
                .withPositiveButton("删除") {
                    App.get().passportRepository.removeBeneficiaryAddress(ba)
                    true
                }
                .withNegativeButton()
                .assembleAndShow()
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
                navigateTo(AddBeneficiaryAddressActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
