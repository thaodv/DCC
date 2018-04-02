package io.wexchain.android.dcc

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.EthsHelper
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.auth.R
import io.wexchain.dccchainservice.domain.Result

class CreatePassportActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_passport)
        initToolbar()

        ensurePassportIsAbsent()

        findViewById<View>(R.id.btn_create_passport).setOnClickListener {
            findViewById<EditText>(R.id.et_input_password)?.text?.let {
                doCreatePassport(it.toString())
            }
        }
    }

    private fun doCreatePassport(password: String) {
        require(password.isNotBlank())
        Single.just(password)
                .observeOn(Schedulers.computation())
                .map {
                    val ethsCredentials = EthsHelper.createNewCredential()
                    val authKey = EthsHelper.createAndroidRSAKeyPair().let {
                        AuthKey(it.second,it.first.public.encoded)
                    }
                    ethsCredentials to authKey
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    App.get().chainGateway.getTicket()
                            .compose(Result.checked())
                            .flatMap { ticket->
                                PassportOperations.uploadPubKeyChecked(it,ticket.ticket,null)
                            }
                }
                .doOnSuccess {
                    App.get().passportRepository.savePassport(it.first,password,it.second)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //todo
                    toast("创建成功")
                    finish()
                },{
                    it.printStackTrace()
                })
    }

    private fun ensurePassportIsAbsent() {
        if (App.get().passportRepository.passportExists){
            // post action to complete onCreate() and postpone finish
            postOnMainThread {
                toast("已存在通行证")
                finish()
            }
        }
    }
}
