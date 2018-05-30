package io.wexchain.android.dcc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R

class CreateScfAccountActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_scf_account)
        initToolbar(false)
        initClicks()
    }

    private fun initClicks() {
        findViewById<View>(R.id.btn_confirm).setOnClickListener {
            val code = findViewById<EditText>(R.id.et_input_invite_code).text.toString()
            registerScfAccount(if(code.isEmpty()) null else code)
        }
    }

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
                App.get().passportRepository.scfAccountExists = true
                navigateTo(PassportCreationSucceedActivity::class.java){
                    putExtra(Extras.FROM_IMPORT,intent.getBooleanExtra(Extras.FROM_IMPORT,false))
                }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_scf_account,menu)
        return true
    }

    override fun onBackPressed() {
        toast("请输入邀请码或点击跳过")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.action_skip ->{
                registerScfAccount(null)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }
}
