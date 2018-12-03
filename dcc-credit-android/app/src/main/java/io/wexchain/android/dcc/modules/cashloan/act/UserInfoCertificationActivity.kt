package io.wexchain.android.dcc.modules.cashloan.act

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityUserinfoCertificationBinding
import io.wexchain.dcc.databinding.DialogListTagBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initClick()
    }

    private fun initClick() {
        binding.marriagesTatus.onClick {
            showTagDialog(MarriageList) {
                binding.marriagesTatus.text = it
            }
        }
    }

    private fun initVm() {
        binding.asAddressVm = getViewModel("asAddressVm")
        binding.asEnterpriseVm = getViewModel("asEnterpriseVm")
        binding.asAddressVm!!.init(this)
        binding.asEnterpriseVm!!.init(this)
    }

    override fun onStop() {
        super.onStop()
        saveDataToSdcard()
    }

    private fun saveDataToSdcard() {
//        CertificationInfo()
    }


    fun showTagDialog(list: ArrayList<String>, data: (String) -> Unit) {
       /* val dialog = ListTagDialog.create(list)
        dialog.show(supportFragmentManager, null)
        dialog.onItemClick = data*/

        val adapter = SimpleDataBindAdapter<ItemTagBinding, String>(
                layoutId = R.layout.item_tag,
                variableId = BR.name,
                itemViewClickListener = object : ItemViewClickListener<String> {
                    override fun onItemClick(item: String?, position: Int, viewId: Int) {
                        data(item!!)
                        bankDialog?.dismiss()
                    }
                }
        )
        adapter.setList(list)

        var dialog = bankDialog
        if (dialog == null) {
            dialog = Dialog(this,R.style.FullWidthWhiteDialog).apply {
                setContentView(R.layout.dialog_list_tag)
                window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                this.findViewById<RecyclerView>(R.id.rv_tag)!!.adapter = adapter
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