package io.wexchain.android.dcc.modules.cashloan.act

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat
import android.view.*
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/10/17.
 */
class UserInfoCertificationActivity : BaseCompatActivity() {

    private val MarriageList = listOf("未婚", "已婚", "离异")
    private val LoanPurposeList = listOf("房屋支出", "看病就医", "家装指出", "旅游指出", "网购消费")
    private val WorkCategoryList = listOf("工薪", "企业主", "个体户", "自由职业")
    private val RelationList = listOf("父亲", "母亲", "儿子", "女儿", "兄弟", "姐妹", "配偶", "同学", "亲戚", "同事", "朋友", "其他")
    private val WorkIndustryList = listOf("商业、服务人员", "专业技术人员", "办事人员、文员、行政等", "工业、生产、运输人员", "农、林、牧、渔、水利业人员",
            "前线销售人员", "国际机关、企事业单位管理人员", "军人")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo_certification)
        initToolbar()
    }

    override fun onStop() {
        super.onStop()
        saveDataToSdcard()
    }

    private fun saveDataToSdcard() {
//        CertificationInfo()
    }

    class ListTagDialog :DialogFragment(){

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.dialog_list_tag, container, false)
            ViewCompat.setElevation(view, resources.getDimension(R.dimen.dialog_elevation))
            return view
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc)
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.window.apply {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            }
            return dialog
        }

        private var host: UserInfoCertificationActivity? = null

        companion object {
            fun create(activity: UserInfoCertificationActivity): ListTagDialog {
                val dialog = ListTagDialog()
                dialog.host = activity
                return dialog
            }
        }
    }
}