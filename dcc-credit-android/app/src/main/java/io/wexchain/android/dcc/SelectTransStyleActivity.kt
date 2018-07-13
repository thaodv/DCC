package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.TransAddressBookAdapter
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivitySelectTransStyleBinding
import io.wexchain.digitalwallet.DigitalCurrency

class SelectTransStyleActivity : BindActivity<ActivitySelectTransStyleBinding>(), ItemViewClickListener<BeneficiaryAddress> {

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

    override val contentLayoutId: Int = R.layout.activity_select_trans_style

    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = "转账"

        mRecyclerView = findViewById(R.id.rv_list)

        initClick()

        App.get().passportRepository.listLatestUsedAddressBook().observe(this, Observer {
            val adapter = TransAddressBookAdapter(this)
            adapter.setList(it)
            mRecyclerView!!.adapter = adapter
        })

    }


    private fun initClick() {
        binding.rlAddressBook.setOnClickListener {
            startActivity(Intent(this, BeneficiaryAddressesManagementActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc).putExtra("usage", 2)
            })
            finish()
        }

        binding.rlWalletAddress.setOnClickListener {
            startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
            })
            finish()
        }

    }

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {

        item?.let {
            when (viewId) {
                R.id.tv_add_to_address -> {
                    if (item.is_added == 0) {
                        startActivity(Intent(this, AddBeneficiaryAddressActivity::class.java).apply {
                            putExtra("address", item.address).putExtra("name", item.shortName).putExtra("avatar", item.avatarUrl).putExtra("added", 1)
                            finish()
                        })
                    } else {
                        startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc).putExtra(Extras.EXTRA_SELECT_ADDRESS, item)
                        })
                    }
                }
            }
        }
    }


}
