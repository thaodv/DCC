package io.wexchain.android.dcc.modules.addressbook.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.wexchain.android.common.resultOk
import io.wexchain.android.dcc.App
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemChooseBeneficiaryAddressBinding

class ChooseAddressBookActivity : BaseCompatActivity(), ItemViewClickListener<AddressBook> {

    private val adapter = SimpleDataBindAdapter<ItemChooseBeneficiaryAddressBinding,AddressBook>(
        layoutId = R.layout.item_choose_address_book,
        variableId = BR.ba,
        itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_address_book)
        initToolbar()
        App.get().passportRepository.addressBooks.observe(this, Observer {
            adapter.setList(it)
        })
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
    }

    override fun onItemClick(item: AddressBook?, position: Int, viewId: Int) {
        item?.let {
            resultOk {
                putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS,it)
            }
        }
    }
}
