package io.wexchain.android.dcc.modules.passport

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.modules.other.LoadingActivity
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportRemovalBinding
import io.wexchain.ipfs.utils.io_main

class PassportRemovalActivity : BaseCompatActivity() {

    private lateinit var binding: ActivityPassportRemovalBinding
    private val passport by lazy {
        App.get().passportRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passport_removal)
        initToolbar(true)
        val protect: Protect = getViewModel()

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

    override fun onResume() {
        super.onResume()
        getCloudToken()
    }

    private fun getCloudToken() {
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .subscribeBy {
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    binding.btnBackupData.onClick {
                        if (it.isEmpty()) {
                            navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", SettingActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                    }
                                }
                            }
                        }
                    }
                }
    }

    private fun toBackup() {
        startActivity(Intent(this, PassportExportActivity::class.java))
    }

    private fun showConfirmDeleteDialog() {
        val dialog = BaseDialog(this)
        dialog.removePassportDialog()
                .onClick(
                        onCancle = {
                            dialog.dismiss()
                            toBackup()
                        },
                        onConfirm = {
                            dialog.dismiss()
                            performPassportDelete()
                        })


        /*val view = layoutInflater.inflate(R.layout.dialog_notice_remove_passport, null)
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
                .assembleAndShow()*/
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
            getSharedPreferences("setting", Context.MODE_PRIVATE).edit().clear().apply()
            getSharedPreferences(Extras.SP_SELECTED_NODE_FILE, Context.MODE_PRIVATE).edit().clear().apply()
            //clear session token
            App.get().scfTokenManager.scfToken = null
            App.get().gardenTokenManager.gardenToken = null
            App.get().userInfo = null
            App.get().passportRepository.deleteAllAddressBook()
            SharedPreferenceUtil.save(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING, null)
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

    private fun finishAllAndRestart() {
        val intent = Intent(this, LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }
}
