package worhavah.regloginlib.passportutil

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import io.reactivex.Single
import io.wexchain.android.common.UrlManage
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import wex.regloginlib.R
import wex.regloginlib.databinding.ActivityPassportRemovalBinding
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.PassportRepository
import worhavah.regloginlib.tools.CustomDialog
import worhavah.regloginlib.tools.PassportOperations
import worhavah.regloginlib.vm.Protect
import RxBus
import EventMsg



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
       //  showLoadingDialog()

        /* RxBus.getInstance().toObservable().map{
             @Throws(Exception::class)
             fun apply(o: Any): EventMsg {
                 return o as EventMsg
             }
         }.subscribe( {
             @Throws(Exception::class)
             fun accept(eventMsg: EventMsg?) {
                 if (eventMsg != null&&eventMsg.equals("finishApp")) {
                     finishAllAndRestart()
                 }
             }
         })*/
    }

    private fun toBackup() {
     //   startActivity(Intent(this, PassportExportActivity::class.java))
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
            PassportRepository.letinit(this).removeEntirePassportInformation()
            LocalProtect.disableProtect()
            //clear rsa keys
            PassportOperations.deleteAllLocalAndroidRSAKeys()
            //clear cert data
           // UrlManage.clearAllCertData()
            //clear session token
            val eventMsg = EventMsg()
            eventMsg.msg = "clearAllCertData"
            RxBus.getInstance().post(eventMsg)

            Networkutils.scfTokenManager.scfToken = null
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
       /* val intent = Intent().apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            action = Intent.ACTION_MAIN
           setPackage(this@PassportRemovalActivity.application.packageName)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/

        val intent  = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)

        finishAffinity()
    }
}
