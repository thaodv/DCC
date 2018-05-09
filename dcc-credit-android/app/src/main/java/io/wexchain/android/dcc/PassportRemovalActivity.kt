package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import io.reactivex.Single
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportRemovalBinding

class PassportRemovalActivity : BaseCompatActivity() {

    private lateinit var binding: ActivityPassportRemovalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passport_removal)
        initToolbar(true)
        val protect = ViewModelProviders.of(this).get(Protect::class.java)
        protect.sync(this)
        VerifyProtectFragment.serve(protect, this)
        binding.btnBackup.setOnClickListener {
            toBackup()
        }
        binding.btnRemove.setOnClickListener {
            if (protect.type.get() == null) {
                showConfirmDeleteDialog()
            } else {
                protect.verifyProtect {
                    performPassportDelete()
                }
            }
        }
    }

    private fun toBackup() {
        startActivity(Intent(this, PassportExportActivity::class.java))
    }

    private fun showConfirmDeleteDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_notice_remove_passport, null)
        view.findViewById<View>(R.id.tv_passport_backup).setOnClickListener {
            toBackup()
        }
        CustomDialog(this)
            .apply {
                setTitle(R.string.title_remove_passport)
                viewContent = view
                withPositiveButton(getString(R.string.title_remove_passport)) {
                    performPassportDelete()
                    true
                }
                withNegativeButton()
            }
            .assembleAndShow()
    }

    private fun performPassportDelete() {
        Single.fromCallable {
            //clear passport info
            App.get().passportRepository.removeEntirePassportInformation()
            LocalProtect.disableProtect()
            //clear rsa keys
            PassportOperations.deleteAllLocalAndroidRSAKeys()
            //clear cert data
            CertOperations.clearAllCertData()
            //clear session token
            App.get().scfTokenManager.scfToken = null
        }
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe { _ ->
                finishAllAndRestart()
            }
    }

    fun finishAllAndRestart() {
        val intent = Intent(this, LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }
}
