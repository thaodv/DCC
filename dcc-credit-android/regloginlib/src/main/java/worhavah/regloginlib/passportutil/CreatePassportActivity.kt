package worhavah.regloginlib.passportutil


import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras2
import worhavah.regloginlib.tools.isPasswordValid
import wex.regloginlib.R
import wex.regloginlib.databinding.ActivityCreatePassportBinding
import worhavah.regloginlib.PassportRepository
import worhavah.regloginlib.tools.InputPasswordVm
import worhavah.regloginlib.tools.PassportOperations
import worhavah.regloginlib.tools.ScfOperations

class CreatePassportActivity : BindActivity<ActivityCreatePassportBinding>() {
    override val contentLayoutId: Int = R.layout.activity_create_passport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        PassportRepository.letinit(this)
        ensurePassportIsAbsent()

        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set("设置数字钱包密码")
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw?:return@setOnClickListener
            if(isPasswordValid(pw)) {
                doCreatePassport(pw)
               // navigateTo(PassportCreationSucceedActivity::class.java)
            }else{
                toast("设置钱包密码不符合要求,请重试")
            }
        }
       /* binding.tvBackupNotice.text = SpannableString("成功创建数字钱包后,请即时在设置->备份数字钱包 中进行备份,以防数字资产丢失").apply {
            val drawable = ContextCompat.getDrawable(this@CreatePassportActivity, R.drawable.ic_settings)!!
            val height = binding.tvBackupNotice.lineHeight
            drawable.setBounds(0,0,height,height)
            setSpan(ImageSpan(drawable),16,17, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }*/
    }

    private fun doCreatePassport(password: String) {
        PassportOperations.createNewAndEnablePassport(password,this)
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    onCreateSuccess()
                }) {
                    it.printStackTrace()
                    toast("创建失败"+it.message)
                }
    }

    //创建云金融账户
    private fun registerScfAccount(code: String?) {
        ScfOperations.registerWithCurrentPassport(code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe{_->
                    PassportRepository.letinit(this).scfAccountExists = true
                    finish()
                    navigateTo(PassportCreationSucceedActivity::class.java){
                        putExtra(Extras2.FROM_IMPORT,intent.getBooleanExtra(Extras2.FROM_IMPORT,false))
                    }
                }
    }

    private fun onCreateSuccess() {
        toast("创建成功")
        finish()
        navigateTo(PassportCreationSucceedActivity::class.java)
    }

    private fun ensurePassportIsAbsent() {
        if (PassportRepository.letinit(this).passportExists){
            // post action to complete onCreate() and postpone finish
            postOnMainThread {
                toast("已存在钱包")
                finish()
            }
        }
    }


}
