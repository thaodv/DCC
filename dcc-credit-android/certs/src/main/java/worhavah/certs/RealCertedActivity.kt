package worhavah.certs

import android.graphics.Color
import android.os.Bundle
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.databinding.ActivityPhonecertBinding
import worhavah.certs.databinding.ActivityPhonecertedBinding
import worhavah.certs.databinding.ActivityRealcertedBinding
import worhavah.certs.tools.CertOperations
import worhavah.certs.tools.CertOperations.savePNCertData
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.tools.CustomDialog
import worhavah.regloginlib.tools.ScfOperations
import java.util.concurrent.TimeUnit

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
