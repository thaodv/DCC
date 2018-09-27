package io.wexchain.android.dcc.modules.trans.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.CreateTransactionActivity
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.addressbook.activity.AddAddressBookActivity
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.TransAddressBookAdapter
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivitySelectTransStyleBinding
import io.wexchain.digitalwallet.DigitalCurrency

class SelectTransStyleActivity : BindActivity<ActivitySelectTransStyleBinding>(), ItemViewClickListener<TransRecord> {

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

    override val contentLayoutId: Int = R.layout.activity_select_trans_style

    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = getString(R.string.transfer)

        mRecyclerView = findViewById(R.id.rv_list)

        initClick()

        App.get().passportRepository.getTransRecord().observe(this, Observer {
            val adapter = TransAddressBookAdapter(this)
            adapter.setList(it)
            mRecyclerView!!.adapter = adapter
        })
    }

    private fun initClick() {
        binding.rlAddressBook.setOnClickListener {
            startActivity(Intent(this, AddressBookActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc).putExtra("usage", 2)
            })
        }

        binding.rlWalletAddress.setOnClickListener {
            startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
            })
        }
    }

    override fun onItemClick(item: TransRecord?, position: Int, viewId: Int) {

        item?.let {

            when (viewId) {
                R.id.tv_add_to_address -> {
                    if (item.is_add == 0) {
                        startActivity(Intent(this, AddAddressBookActivity::class.java).apply {
                            putExtra(Extras.EXTRA_TRANSRECORE, item)
                        })
                    } else {
                        startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
                                    .putExtra(Extras.EXTRA_SELECT_TRANSRECORD, item)
                        })
                    }
                }
                else -> {
                    startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                        putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
                                .putExtra(Extras.EXTRA_SELECT_TRANSRECORD, item)
                    })
                }
            }
        }

    }


}
