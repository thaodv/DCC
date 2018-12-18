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
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.cashloan.bean.CertificationInfo
import io.wexchain.android.dcc.modules.cashloan.vm.AddressVm
import io.wexchain.android.dcc.modules.cashloan.vm.ContactsVm
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
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


    companion object {
        const val contactsVm1 = "contactsVm1"
        const val contactsVm2 = "contactsVm2"
        const val contactsVm3 = "contactsVm3"
        val MarriageList = arrayListOf("未婚", "已婚", "离异", "已婚未育", "丧偶", "其他")
        val LoanPurposeList = arrayListOf("房屋支出", "看病就医", "家装指出", "旅游消费", "网购消费", "购买农资")
        val WorkCategoryList = arrayListOf("私企公司职工", "国企事业单位职工", "公务员", "自雇创业人员", "农林牧副渔人员", "军人", "自由职业者", "失业人员", "退休人员", "家庭主妇")
        val RelationList = arrayListOf("父亲", "配偶", "母亲", "子女", "同学", "朋友", "同事", "亲戚", "儿子", "女儿", "兄弟", "姐妹", "其他")
        val WorkIndustryList = arrayListOf("商业、服务人员", "专业技术人员", "办事人员、文员、行政等", "工业、生产、运输人员", "农、林、牧、渔、水利业人员",
                "前线销售人员", "国际机关、企事业单位管理人员", "军人", "在校学生")
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
                saveDataToSdcard(true) {
                    toast(it)
                }
            }
        }
        toolbar?.setNavigationOnClickListener {
            saveDataToSdcard()
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
        setData()
    }

    private fun setData() {
        val infoCert = App.get().passportRepository.getUserInfoCert()
        infoCert?.let {
            val bean = it.toBean(CertificationInfo::class.java)
            binding.marriagesTatus.text = bean.MarriageStatus
            bean.ResideProvince?.let {
                binding.asAddressVm!!.province.set(it)
                binding.asAddressVm!!.city.set(bean.ResideCity)
                binding.asAddressVm!!.area.set(bean.ResideArea)
            }
            bean.ResideAddress?.let {
                binding.detailAddress.setText(it)
            }
            bean.LoanPurpose?.let {
                binding.loanpurposeTatus.text = it
            }
            bean.WorkCategory?.let {
                binding.workcategoryStatus.text = it
            }
            bean.WorkIndustry?.let {
                binding.workindustryStatus.text = it
            }
            bean.WorkYear?.let {
                binding.workYear.setText(it)
            }
            bean.CompanyProvince?.let {
                binding.asEnterpriseVm!!.province.set(it)
                binding.asEnterpriseVm!!.city.set(bean.CompanyCity)
                binding.asEnterpriseVm!!.area.set(bean.CompanyArea)
            }
            bean.CompanyAddress?.let {
                binding.workAddress.setText(it)
            }
            bean.CompanyName?.let {
                binding.workName.setText(it)
            }
            bean.CompanyPhone?.let {
                binding.workPhone.setText(it)
            }
            bean.Contacts1Relation?.let {
                binding.contactsVm1!!.relation.set(it)
            }
            bean.Contacts1Name?.let {
                binding.contactsVm1!!.olderName.set(it)
                binding.contactsVm1!!.name.set(it)
            }
            bean.Contacts1Phone?.let {
                binding.contactsVm1!!.olderPhone.set(it)
                binding.contactsVm1!!.phone.set(it)
            }
            bean.Contacts2Relation?.let {
                binding.contactsVm2!!.relation.set(it)
            }
            bean.Contacts2Name?.let {
                binding.contactsVm2!!.olderName.set(it)
                binding.contactsVm2!!.name.set(it)
            }
            bean.Contacts2Phone?.let {
                binding.contactsVm2!!.olderPhone.set(it)
                binding.contactsVm2!!.phone.set(it)
            }
            bean.Contacts3Relation?.let {
                binding.contactsVm3!!.relation.set(it)
            }
            bean.Contacts3Name?.let {
                binding.contactsVm3!!.olderName.set(it)
                binding.contactsVm3!!.name.set(it)
            }
            bean.Contacts3Phone?.let {
                binding.contactsVm3!!.olderPhone.set(it)
                binding.contactsVm3!!.phone.set(it)
            }
        }
    }

    override fun onBackPressed() {
        saveDataToSdcard()
    }

    private fun saveDataToSdcard(check: Boolean = false, error: (String) -> Unit = {}) {
        val marriages = binding.marriagesTatus.text.toString()
        val isSelect = binding.asAddressVm!!.isSelect()
        val detailAddress = binding.asAddressVm!!.getAddress()
        val address = binding.detailAddress.text.toString()
        val loanpurpose = binding.loanpurposeTatus.text.toString()
        val workcategory = binding.workcategoryStatus.text.toString()
        val workindustry = binding.workindustryStatus.text.toString()
        val workYear = binding.workYear.text.toString()
        val isSelect2 = binding.asEnterpriseVm!!.isSelect()
        val companyAddress = binding.asEnterpriseVm!!.getAddress()
        val workAddress = binding.workAddress.text.toString()
        val workName = binding.workName.text.toString()
        val workPhone = binding.workPhone.text.toString()
        val check1 = binding.contactsVm1!!.check()
        val relation1 = binding.contactsVm1!!.relation.get()
        val name1 = binding.contactsVm1!!.name.get()
        val phone1 = binding.contactsVm1!!.phone.get()

        val check2 = binding.contactsVm2!!.check()
        val relation2 = binding.contactsVm2!!.relation.get()
        val name2 = binding.contactsVm2!!.name.get()
        val phone2 = binding.contactsVm2!!.phone.get()

        val check3 = binding.contactsVm3!!.check()
        val relation3 = binding.contactsVm3!!.relation.get()
        val name3 = binding.contactsVm3!!.name.get()
        val phone3 = binding.contactsVm3!!.phone.get()

        if (check) {
            if (!checkSeclected(marriages)) {
                error("请选择婚姻状况")
                return
            }
            if (!isSelect) {
                error("请选择居住地址")
                return
            }
            if (address.isEmpty()) {
                error("请填写居住地址")
                return
            }
            if (!checkSeclected(loanpurpose)) {
                error("请选择借款用途")
                return
            }
            if (!checkSeclected(workcategory)) {
                error("请选择工作类别")
                return
            }
            if (!checkSeclected(workindustry)) {
                error("请选择工作行业")
                return
            }
            if (workYear.isEmpty()) {
                error("请填写工作年限")
                return
            }
            if (!isSelect2) {
                error("请选择企业所在地址")
                return
            }
            if (workAddress.isEmpty()) {
                error("请填写办公地址")
                return
            }
            if (workName.isEmpty()) {
                error("请填写单位全称")
                return
            }
            if (!check1) {
                toast("请填写完整联系人1信息")
                return
            }
            if (!check2) {
                toast("请填写完整联系人2信息")
                return
            }
            if (!check3) {
                toast("请填写完整联系人3信息")
                return
            }
        }

        val data = CertificationInfo(marriages, detailAddress.first, detailAddress.second, detailAddress.third, address, loanpurpose,
                workcategory, workindustry, workYear, companyAddress.first, companyAddress.second, companyAddress.third, workAddress,
                workName, workPhone, relation1, phone1, name1, relation2, phone2, name2, relation3, phone3, name3)
        val json = data.toJson()
        App.get().passportRepository.setUserInfoCert(json)
        finish()
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