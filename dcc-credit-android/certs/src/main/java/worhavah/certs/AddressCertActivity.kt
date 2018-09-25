package worhavah.certs

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import worhavah.certs.beans.Beancitys
import worhavah.certs.databinding.ActivityAddresscertBinding
import worhavah.certs.databinding.ActivityMailcertBinding
import worhavah.certs.databinding.ActivityPhonecertBinding
import worhavah.certs.tools.CertOperations
import worhavah.certs.tools.CertOperations.getcertHANum
import worhavah.certs.views.ChangeAddressPopwindow
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.tools.ScfOperations
import java.util.HashMap
import java.util.concurrent.TimeUnit

class AddressCertActivity : BindActivity<ActivityAddresscertBinding>(){
    override val contentLayoutId: Int
        get() = R.layout.activity_addresscert
    var emailAddress=""
    private  var orderId :Long=0
    private lateinit var realName: String
    private lateinit var realId: String
    private lateinit var passport: Passport
    private lateinit var mChangeAddressPopwindow :ChangeAddressPopwindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //checkPreconditions()
        initToolbar()
        //toolbar!!.title="验证手机号码"
        if(!getcertHANum().isNullOrEmpty()){
            binding.etPn.text= CertOperations.certPrefs.certHALine1.get()
            binding.etValid.setText(CertOperations.certPrefs.certHALine2.get())
        }
        setclick()

    }


     lateinit var pl: Array<String>
    private  var cm= HashMap<String, Array<String>>()
    private  var cbm= HashMap<String, Array<Beancitys.ContentBean>>()
    private  var am= HashMap<String, Array<String>>()
    private  var abm= HashMap<String, Array<Beancitys.ContentBean>>()
 //   private  var ppm= HashMap<String, String>()
 //   private  var cpm= HashMap<String, String>()
    private  var apm= HashMap<String, String>()
    private var canShow:Boolean=false
    var choicedadd=""
    var inputdadd=""
    var choicedaddCode=""
    private fun setclick() {
        CertOperations.insCertApi.getJsonString().observeOn(AndroidSchedulers.mainThread()).subscribe(
            {
            var ll=    it.children
                pl=ll.map { it.content.name }.toTypedArray()
                //ppm.put(it.content.name,it.content.code)
                for (i in ll){
                    cm.put(i.content.name,i.children.map { it.content.name }.toTypedArray())
                    cbm.put(i.content.name,i.children.map { it.content }.toTypedArray())
                   // cpm.put(i.content.name,i.content.code)
                    for(s in i.children){
                        am.put(s.content.name,s.children.map { it.content.name }.toTypedArray())
                        abm.put(s.content.name,s.children.map { it.content }.toTypedArray())
                        for (n in s.children){
                            apm.put(n.content.name,n.content.code)
                        }
                    }
                }
                mChangeAddressPopwindow=ChangeAddressPopwindow(this@AddressCertActivity,pl,cm,am)
                canShow=true
                startChoice()
            },{

            }
        )
        binding.etPn.setOnClickListener {
            if (canShow){
                startChoice()
            }
        }
        binding.btSubmit.setOnClickListener {
            if (binding.etValid.text.length<2){
                toast("请输入详细地址")
            }else{
                inputdadd=binding.etValid.text.toString()
                submitVerify(Networkutils.passportRepository.getCurrentPassport()!!,binding.etPn.text.toString()+"\n"+inputdadd)
            }
        }
    }


    fun startChoice(){
        mChangeAddressPopwindow.showAtLocation(binding.etPn, Gravity.BOTTOM, 0, 0)
        mChangeAddressPopwindow
            .setAddresskListener(object : ChangeAddressPopwindow.OnAddressCListener {
                override fun onClick(province: String, city: String, area: String) {
                    choicedadd=province +" "+ city +" "+ area
                    choicedaddCode=apm.get(area)!!
                    binding.etPn.setText(choicedadd)
                }
            })
    }


    private fun goVertify(){
        ScfOperations
            .withScfTokenInCurrentPassport {
                CertOperations.insCertApi.mailverifadvance(it,this.orderId,binding.etPn.text.toString(),binding.etValid.text.toString())
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                toast("验证成功")
                CertOperations.saveEmCertData(it)
                navigateTo(MailCertedActivity::class.java)
                finish()
            }, { it.printStackTrace()
                toast("验证失败")
            })
    }
    private fun submitVerify(passport: Passport, address: String) {
        CertOperations.homeAddressCert(passport, address,  { this.orderId = it },"HOME_ADDRESS")
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
               // hideLoadingDialog()
            }
            .subscribe({order->
                ScfOperations
                    .withScfTokenInCurrentPassport {
                        CertOperations.insCertApi.homeAddressveri(it,this.orderId,address,choicedaddCode)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                         hideLoadingDialog()
                    }
                    .subscribe({
                        toast("保存成功")
                        CertOperations.saveHomeAddressData(it,binding.etPn.text.toString(),inputdadd)
                        navigateTo(AddressCertedActivity::class.java)
                        finish()
                    }, { it.printStackTrace()})
            })
    }



}
