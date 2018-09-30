package worhavah.certs

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.base.BindActivity
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.databinding.ActivityRealcertedBinding
import worhavah.certs.tools.CertOperations
import worhavah.regloginlib.tools.CustomDialog

class RealCertedActivity : BindActivity<ActivityRealcertedBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_realcerted


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        setclick()

    }

    private fun setclick() {
        binding.etValid.text=CertOperations.certPrefs.certRealId.get().toString()
        binding.etPn.text=CertOperations.certPrefs.certRealName.get().toString()
        binding.validtime.text="验证时间： "+CertOperations.certPrefs.certIdTime.get().toString()


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
