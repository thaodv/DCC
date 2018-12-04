package io.wexchain.android.dcc.modules.cashloan.act

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.widget.TextView
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.modules.cashloan.vm.AddressVm
import io.wexchain.android.dcc.modules.cashloan.vm.ContactsVm
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityUserinfoCertificationBinding
import io.wexchain.dcc.databinding.ItemTagBinding

/**
 *Created by liuyang on 2018/10/17.
 */
class UserInfoCertificationActivity : BindActivity<ActivityUserinfoCertificationBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_userinfo_certification

    private var bankDialog: Dialog? = null

    private val MarriageList = arrayListOf("未婚", "已婚", "离异")
    private val LoanPurposeList = arrayListOf("房屋支出", "看病就医", "家装指出", "旅游指出", "网购消费")
    private val WorkCategoryList = arrayListOf("工薪", "企业主", "个体户", "自由职业")
    private val RelationList = arrayListOf("父亲", "母亲", "儿子", "女儿", "兄弟", "姐妹", "配偶", "同学", "亲戚", "同事", "朋友", "其他")
    private val WorkIndustryList = arrayListOf("商业、服务人员", "专业技术人员", "办事人员、文员、行政等", "工业、生产、运输人员", "农、林、牧、渔、水利业人员",
            "前线销售人员", "国际机关、企事业单位管理人员", "军人")

    companion object {
        const val contactsVm1 = "contactsVm1"
        const val contactsVm2 = "contactsVm2"
        const val contactsVm3 = "contactsVm3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initClick()
    }

    private fun initClick() {
        binding.apply {
            marriagesTatus.selectTag(MarriageList)
            loanpurposeTatus.selectTag(LoanPurposeList)
            workcategoryStatus.selectTag(WorkCategoryList)
            workindustryStatus.selectTag(WorkIndustryList)
            certCommit.onClick {
                val marriages = binding.marriagesTatus.text.toString()
                if (!checkSeclected(marriages)) {
                    toast("请选择婚姻状况")
                    return@onClick
                }
                val isSelect = binding.asAddressVm!!.isSelect()
                if (!isSelect) {
                    toast("请选择居住地址")
                    return@onClick
                }
                val detailAddress = binding.asAddressVm!!.getAddress()

                val address = binding.detailAddress.text.toString()
                if (address.isEmpty()) {
                    toast("请填写居住地址")
                    return@onClick
                }
                val loanpurpose = binding.loanpurposeTatus.text.toString()
                if (!checkSeclected(loanpurpose)) {
                    toast("请选择借款用途")
                    return@onClick
                }


            }
        }
    }

    private fun checkSeclected(txt: String): Boolean {
        return if (txt.isEmpty()) {
            false
        } else {
            txt != "请选择"
        }
    }

    private fun TextView.selectTag(list: ArrayList<String>) {
        this.onClick {
            data = {
                adapter.setList(emptyList())
                this.text = it
            }
            showTagDialog(list)
        }
    }

    private fun ContactsVm.observeRelation() {
        relationCall.observe(this@UserInfoCertificationActivity, Observer {
            data = {
                adapter.setList(emptyList())
                relation.set(it)
            }
            showTagDialog(RelationList)
        })
    }

    private fun initVm() {
        binding.asAddressVm = getViewModel<AddressVm>("asAddressVm")
                .apply { init(this@UserInfoCertificationActivity) }
        binding.asEnterpriseVm = getViewModel<AddressVm>("asEnterpriseVm")
                .apply { init(this@UserInfoCertificationActivity) }

        binding.contactsVm1 = getViewModel<ContactsVm>(contactsVm1)
                .apply {
                    title.set("联系人1")
                    observeRelation()
                }
        binding.contactsVm2 = getViewModel<ContactsVm>(contactsVm2)
                .apply {
                    title.set("联系人2")
                    observeRelation()
                }
        binding.contactsVm3 = getViewModel<ContactsVm>(contactsVm3)
                .apply {
                    title.set("联系人3")
                    observeRelation()
                }
    }

    override fun onStop() {
        super.onStop()
        saveDataToSdcard()
    }

    private fun saveDataToSdcard() {
//        CertificationInfo()
    }

    val adapter = SimpleDataBindAdapter<ItemTagBinding, String>(
            layoutId = R.layout.item_tag,
            variableId = BR.name,
            itemViewClickListener = object : ItemViewClickListener<String> {
                override fun onItemClick(item: String?, position: Int, viewId: Int) {
                    data?.invoke(item!!)
                    bankDialog?.dismiss()
                }
            }
    )
    var data: ((String) -> Unit)? = null

    private fun showTagDialog(list: ArrayList<String>) {
        adapter.setList(list)

        var dialog = bankDialog
        if (dialog == null) {
            dialog = Dialog(this, R.style.FullWidthWhiteDialog).apply {
                setContentView(R.layout.dialog_list_tag)
                window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                val view = this.findViewById<RecyclerView>(R.id.rv_tag)
                view.adapter = adapter
            }
            bankDialog = dialog
        }
        if (dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }

}