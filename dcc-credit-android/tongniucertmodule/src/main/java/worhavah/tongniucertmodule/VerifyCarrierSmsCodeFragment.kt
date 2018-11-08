package worhavah.tongniucertmodule

import android.os.Bundle
import android.view.View
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.FragmentTnverifyCarrierSmsCodeBinding
import java.util.concurrent.TimeUnit

class VerifyCarrierSmsCodeFragment : BindFragment<FragmentTnverifyCarrierSmsCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_tnverify_carrier_sms_code

    private var listener: Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = ""//clear
        starttimer()
        binding.btnSubmitCode.setOnClickListener {
            binding.code?.let {
                listener?.onSubmitSmsCode(null, it)
            }
        }
        binding.recode.setOnClickListener {
            starttimer()
            listener?.reCode()!!.observeOn(AndroidSchedulers.mainThread())

                    .subscribe({
                        runOnMainThread {
                            toast("短信已发送")
                        }
                    }, { })
        }
    }

    interface Listener {
        fun onSubmitSmsCode(authCode: String?, smsCode: String?)
        fun reCode(): Single<String>
    }
    fun recode(){
        listener?.reCode()!!.observeOn(AndroidSchedulers.mainThread())

            .subscribe({
                runOnMainThread {

                }
            }, { })
    }
    companion object {
        fun create(listener: Listener): VerifyCarrierSmsCodeFragment {
            val fragment = VerifyCarrierSmsCodeFragment()
            fragment.listener = listener
            return fragment
        }

    }

    var mSubscription: Subscription? = null // Subscription 对象，用于取消订阅关系，防止内存泄露
    private fun starttimer() {
        val count = 59L
        Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .onBackpressureBuffer()//加上背压策略
                .take(count) //设置循环次数
                .map { aLong ->
                    count - aLong //
                }
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(object : Subscriber<Long> {
                    override fun onSubscribe(s: Subscription?) {
                        if (activity != null) {
                            binding.recode.isEnabled = false//在发送数据的时候设置为不能点击
                            binding.recode.setTextColor(resources.getColor(worhavah.certs.R.color.gray_mask))//背景色设为灰色
                            mSubscription = s
                            s?.request(Long.MAX_VALUE)//设置请求事件的数量，重要，必须调用
                        }

                    }

                    override fun onNext(aLong: Long?) {
                        if (activity != null) {
                            binding.recode.text = "${aLong}s后重发" //接受到一条就是会操作一次UI

                        }
                    }

                    override fun onComplete() {
                        if (activity != null) {
                            binding.recode.text = "免费获取"
                            binding.recode.isEnabled = true
                            binding.recode.setTextColor(resources.getColor(worhavah.certs.R.color.purple))//背景色设为灰色
                            mSubscription?.cancel()//取消订阅，防止内存泄漏
                        }

                    }

                    override fun onError(t: Throwable?) {
                        t?.printStackTrace()
                    }
                })
    }

}
