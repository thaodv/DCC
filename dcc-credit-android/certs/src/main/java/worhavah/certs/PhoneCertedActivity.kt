package worhavah.certs

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.base.BindActivity
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.databinding.ActivityPhonecertedBinding
import worhavah.certs.tools.CertOperations
import worhavah.regloginlib.tools.CustomDialog

class PhoneCertedActivity : BindActivity<ActivityPhonecertedBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_phonecerted


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        setclick()

    }

    private fun setclick() {
        binding.etPn.text=CertOperations.getcertPhoneNum()
        binding.validtime.text="验证时间： "+CertOperations.getcertPhoneData()
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
                setTitle("删除手机号")
                textContent = "确认删除绑定手机号吗？"
                withPositiveButton("确认") {
                    CertOperations.savePNCertData(BeanValidResult("",""))
                    navigateTo(PhoneCertActivity::class.java)
                    finish()
                    true
                }
                withNegativeButton()
            }
            .assembleAndShow()
    }


    


}
