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
import worhavah.certs.databinding.ActivityPhonecertBinding
import worhavah.certs.tools.CertOperations
import worhavah.certs.tools.CertOperations.savePNCertData
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.tools.ScfOperations
import java.util.concurrent.TimeUnit

class PhoneCertActivity : BindActivity<ActivityPhonecertBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_phonecert
    var emailAddress=""
    private  var orderId :Long=0
    private lateinit var realName: String
    private lateinit var realId: String
    private lateinit var passport: Passport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        setclick()

    }

    private fun setclick() {

        binding.tvGetcertno.setOnClickListener {
            if (binding.etPn.text.length<2){
                toast("请输入正确手机号码")
            }else{
                starttimer()
                submitVerify(Networkutils.passportRepository.getCurrentPassport()!!,binding.etPn.text.toString())
            }
        }
        binding.btSubmit.setOnClickListener {
            if (binding.etPn.text.length<2||binding.etValid.text.length<2){
                toast("请输入正确手机号码验证码")
            }else{
                goVertify()
            }
        }
    }

    private fun checkPreconditions() {
        val cp = Networkutils.passportRepository.getCurrentPassport()
        if (cp?.authKey == null) {
            runOnMainThread {
                Pop.toast(R.string.ca_not_enabled, this@PhoneCertActivity)
               // finish()
            }
            return
        }
        val certId = CertOperations.getCertId()
        if (certId == null) {
            runOnMainThread {
                Pop.toast("身份认证未完成", this@PhoneCertActivity)
              //  finish()
            }
            return
        }
        realName = certId.first
        realId = certId.second
        passport = cp

    }
    private fun goVertify(){
        ScfOperations
            .withScfTokenInCurrentPassport {
                CertOperations.insCertApi.mobileverifadvance(it,this.orderId,binding.etPn.text.toString(),binding.etValid.text.toString())
            }.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                  hideLoadingDialog()
            }
            .subscribe({
                toast("验证成功")
                savePNCertData(it)
                navigateTo(PhoneCertedActivity::class.java)
                finish()
            }, { it.printStackTrace()
                toast("验证失败")
            })
    }
    private fun submitVerify(passport: Passport, address: String) {
        CertOperations.submitCert(passport, address,  { this.orderId = it },"MOBILE")
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe({order->
                ScfOperations
                    .withScfTokenInCurrentPassport {
                        CertOperations.insCertApi.mobileverif(it,this.orderId,binding.etPn.text.toString())
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        toast("验证码已发送")
                    }, { it.printStackTrace()})
            })
    }
    var mSubscription: Subscription? = null // Subscription 对象，用于取消订阅关系，防止内存泄露
    private fun starttimer() {
        val count = 59L
        Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
            .onBackpressureBuffer()//加上背压策略
            .take(count) //设置循环次数
            .map{ aLong ->
                count - aLong //
            }
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe(object : Subscriber<Long> {
                override fun onSubscribe(s: Subscription?) {
                    binding.tvGetcertno.isEnabled = false//在发送数据的时候设置为不能点击
                    binding.tvGetcertno.setTextColor(resources.getColor(R.color.gray_mask))//背景色设为灰色
                    mSubscription = s
                    s?.request(Long.MAX_VALUE)//设置请求事件的数量，重要，必须调用
                }

                override fun onNext(aLong: Long?) {
                    binding.tvGetcertno.text = "${aLong}s后重发" //接受到一条就是会操作一次UI
                }

                override fun onComplete() {
                    binding.tvGetcertno.text = "免费获取"
                    binding.tvGetcertno.isEnabled = true
                    binding.tvGetcertno.setTextColor(resources.getColor(R.color.purple))//背景色设为灰色
                    mSubscription?.cancel()//取消订阅，防止内存泄漏
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                }
            })
    }

    


}
