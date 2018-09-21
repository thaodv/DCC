package worhavah.regloginlib.passportutil

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.tools.isKeyStoreValid
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import wex.regloginlib.R
import wex.regloginlib.databinding.FragmentPasteKeystoreBinding
import worhavah.regloginlib.PassportRepository
import worhavah.regloginlib.tools.InputPasswordVm

/**
 * Created by lulingzhi on 2017/11/17.
 */
class PasteKeystoreFragment : BindFragment<FragmentPasteKeystoreBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_paste_keystore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputPasswordVm = getViewModel<InputPasswordVm>().apply {
            passwordValidator = this@PasteKeystoreFragment::checkKeystorePassword
            passwordHint.set("验证数字钱包密码")
            reset()
        }
        inputPasswordVm.secureChangedEvent.observe(this, Observer {
            binding.executePendingBindings()
        })
        binding.inputPassword = inputPasswordVm
        binding.ivScan.setOnClickListener {
            requestScan()
        }
    }

    private fun checkKeystorePassword(pw: String?) = pw != null && pw.isNotBlank()

    private fun requestScan() {
        startActivityForResult(Intent(context, QrScannerActivity::class.java), RequestCodes.SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                    binding.keyStore = result
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun parsePassport(): Single<Pair<Credentials, String>> {
        val ks = binding.keyStore
        val pw = binding.inputPassword!!.password.get()
        return if (!isKeyStoreValid(ks)){
            Single.error(IllegalArgumentException("KeyStore信息格式错误，导入失败"))
//        }else if (!isPasswordValid(pw)) {
//            Single.error(IllegalArgumentException("密码不符合规则"))
        }else if (pw.isNullOrBlank()) {
            Single.error(IllegalArgumentException("密码不可为空"))
        }else{
            Single.just(Pair(ks!!, pw!!))
                    .observeOn(Schedulers.computation())
                    .map {
                        //parse
                        val walletFile = PassportRepository.parseWalletFile(it.first)
                        walletFile ?: throw IllegalArgumentException("KeyStore信息格式错误")
//                        if(!isWalletInformationComplete(walletFile)){
//                            throw IllegalArgumentException("KEYSTORE格式不正确或信息不完整")
//                        }
                        //try get Credential
                        val ecKeyPair = Wallet.decrypt(it.second, walletFile)
                        Credentials.create(ecKeyPair) to pw
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        binding.inputPassword!!.password.set("")
                    }
        }
    }

    companion object {

        fun create(): PasteKeystoreFragment {
            return PasteKeystoreFragment()
        }
    }
}