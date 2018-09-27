package worhavah.certs

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.base.BindActivity
import worhavah.certs.beans.BeanValidHomeResult
import worhavah.certs.databinding.ActivityAddresscertedBinding
import worhavah.certs.tools.CertOperations
import worhavah.regloginlib.tools.CustomDialog

class AddressCertedActivity : BindActivity<ActivityAddresscertedBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_addresscerted


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        setclick()

    }

    private fun setclick() {
        binding.etPn.text=CertOperations.getcertHANum()
        binding.validtime.text="提交时间： "+CertOperations.getcertHAData()
        binding.tvDelete.setOnClickListener {
            delete()
        }
        binding.ivDelete.setOnClickListener {
            delete()
        }
        binding.tvEdit.setOnClickListener {
            toEdit()
        }
        binding.ivEdit.setOnClickListener {
            toEdit()
        }

    }
    fun toEdit(){
        navigateTo(AddressCertActivity::class.java)
        finish()
    }

    fun delete(){
        CustomDialog(this)
            .apply {
                setTitle("删除地址")
                textContent = "确认删除绑定地址吗？"
                withPositiveButton("确认") {
                    CertOperations.saveHomeAddressData(BeanValidHomeResult("",""),"","")
                    navigateTo(AddressCertActivity::class.java)
                    finish()
                    true
                }
                withNegativeButton()
            }
            .assembleAndShow()
    }


    


}
