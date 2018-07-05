package io.wexchain.android.dcc

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.wexmarket.android.passport.ResultCodes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
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
            if(code.isEmpty()){
                toast("邀请码不能为空")
            }else {
                registerScfAccount(code)
            }
        }
        findViewById<View>(R.id.ib_scan_code).setOnClickListener {
            requestScanCode()
        }
    }

    private fun requestScanCode() {
        startActivityForResult(Intent(this,QrScannerActivity::class.java),RequestCodes.SCAN)
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
                finish()
                navigateTo(PassportCreationSucceedActivity::class.java){
                    putExtra(Extras.FROM_IMPORT,intent.getBooleanExtra(Extras.FROM_IMPORT,false))
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            RequestCodes.SCAN->{
                if(resultCode == ResultCodes.RESULT_OK){
                    val scanText = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                    scanText?.let {
                        findViewById<EditText>(R.id.et_input_invite_code).setText(it)
                    }
                }
            }
            else-> super.onActivityResult(requestCode, resultCode, data)
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
