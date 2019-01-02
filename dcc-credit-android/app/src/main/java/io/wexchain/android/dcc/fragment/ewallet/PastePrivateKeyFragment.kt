package io.wexchain.android.dcc.fragment.ewallet

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.isEcPrivateKeyValid
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentPastePrivateKeyBinding
import io.wexchain.dccchainservice.DccChainServiceException
import org.web3j.crypto.Credentials

/**
 * Created by lulingzhi on 2017/11/17.
 */
class PastePrivateKeyFragment : BindFragment<FragmentPastePrivateKeyBinding>() {
    override val contentLayoutId = R.layout.fragment_paste_private_key

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputPassword = ViewModelProviders.of(this).get(InputPasswordVm::class.java)
        inputPassword.passwordHint.set(context!!.getString(R.string.please_set_passport_password))
        inputPassword.reset()
        inputPassword.secureChangedEvent.observe(this, Observer {
            binding.executePendingBindings()
        })
        binding.inputPassword = inputPassword
        binding.ivScan.setOnClickListener {
            requestScan()
        }
    }

    private fun requestScan() {
        startActivityForResult(Intent(context, QrScannerActivity::class.java), RequestCodes.SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                    binding.key = result.toLowerCase()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun parsePassport(): Single<Pair<Credentials, String>> {
        val key = binding.key
        val pw = binding.inputPassword!!.password.get()
        return if (!isEcPrivateKeyValid(key)) {
            Single.error(DccChainServiceException("私钥明文格式不对，请核对后重新输入！"))
        } else if (!CommonUtils.checkPassword(pw)) {
            Single.error(DccChainServiceException("密码不符合规则"))
        } else {
            Single.just(Pair(key!!, pw!!))
                    .observeOn(Schedulers.computation())
                    .map {
                        val privateKey: String
                        val tmp = it.first.toLowerCase()
                        privateKey = if (tmp.substring(0, 2) == "0x") {
                            tmp.substring(2)
                        } else {
                            tmp
                        }
                        val credentials = Credentials.create(privateKey)
                        credentials to it.second
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    companion object {
        fun create(): PastePrivateKeyFragment {
            return PastePrivateKeyFragment()
        }
    }
}
