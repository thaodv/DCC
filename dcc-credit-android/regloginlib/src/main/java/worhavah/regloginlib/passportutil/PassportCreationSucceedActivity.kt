package worhavah.regloginlib.passportutil

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras2
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.fragment.CreateProtectFragment
import wex.regloginlib.R
import wex.regloginlib.databinding.ActivityPassportCreationSucceedBinding
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.PassportRepository
import worhavah.regloginlib.tools.ScfOperations

class PassportCreationSucceedActivity: BindActivity<ActivityPassportCreationSucceedBinding>() {
    override val contentLayoutId: Int = R.layout.activity_passport_creation_succeed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnEnableProtect.setOnClickListener {
            showEnableProtectDialog()
        }
        binding.btnSkip.setOnClickListener { skip() }
        setupData()
        registerScfAccount(null)
        initData()
    }
    private fun registerScfAccount(code: String?) {
        ScfOperations.registerWithCurrentPassport(code)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
               // showLoadingDialog()
            }
            .doFinally {
              //  hideLoadingDialog()
            }
            .subscribe{_->
                Networkutils.passportRepository.scfAccountExists = true
               val p= Networkutils.passportRepository.getCurrentPassport()
                ScfOperations.loginWithPassport(p?.address, p?.authKey?.getPrivateKey()).subscribe()

                /*Networkutils.passportRepository.scfAccountExists = true
                finish()
                navigateTo(PassportCreationSucceedActivity::class.java){
                    putExtra(Extras.FROM_IMPORT,intent.getBooleanExtra(Extras.FROM_IMPORT,false))
                }*/
            }
    }

    private fun showEnableProtectDialog() {
        CreateProtectFragment.create(object :CreateProtectFragment.CreateProtectFinishListener{
            override fun onCancel(): Boolean {
                skip()
                return true
            }

            override fun cancelText(): String {
                return "跳过"
            }


            override fun onCreateProtectFinish(type: LocalProtectType) {
                toast(R.string.local_protect_enabled)
                createProtectDone(type)
            }
        }).show(supportFragmentManager, null)
    }

    private fun createProtectDone(type: LocalProtectType?) {
        val passport = Networkutils.passportRepository.getCurrentPassport()
        ScfOperations.loginWithPassport(passport?.address, passport?.authKey?.getPrivateKey())
        /*val intent = Intent().apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            action = Intent.ACTION_MAIN
            setPackage(this@PassportCreationSucceedActivity.application.packageName)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/
        val intent  = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)
        finish()
    }


    private fun setupData() {
        binding.fromImport = intent.getBooleanExtra(Extras2.FROM_IMPORT, false)
        PassportRepository.letinit(this).currPassport
                .observe(this, Observer {
                    it?.let {
                        binding.passport = it
                    }
                })
    }
    private fun skip(){
        createProtectDone(null)
    }
    private fun initData() {
        PassportRepository.letinit(this).currPassport
            .observe(this, Observer {
                binding.tvAddress .setText(it?.address)
            })
    }

    override fun onBackPressed() {

    }
}