package worhavah.certs

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import worhavah.certs.beans.BeanValidMailResult
import worhavah.certs.databinding.ActivityMailcertedBinding
import worhavah.certs.tools.CertOperations
import worhavah.regloginlib.tools.CustomDialog

class MailCertedActivity : BindActivity<ActivityMailcertedBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_mailcerted


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        setclick()

    }

    private fun setclick() {
        binding.etPn.text=CertOperations.getcertEmCNum()
        binding.validtime.text="提交时间： "+CertOperations.getcertEmCData()
        binding.tvDelete.setOnClickListener {
            delete()
        }
        binding.ivDelete.setOnClickListener {
            delete()
        }

    }
    fun delete(){
        CustomDialog(this)
            .apply {
                setTitle("删除邮箱")
                textContent = "确认删除绑定邮箱吗？"
                withPositiveButton("确认") {
                    CertOperations.saveEmCertData(BeanValidMailResult("",""))
                    navigateTo(MailCertActivity::class.java)
                    finish()
                    true
                }
                withNegativeButton()
            }
            .assembleAndShow()
    }


    


}
