package io.wexchain.android.dcc

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.google.gson.JsonSyntaxException
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.fragment.PasteKeystoreFragment
import io.wexchain.android.dcc.fragment.PastePrivateKeyFragment
import io.reactivex.Single
import io.wexchain.android.common.*
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityPassportImportBinding
import kotlinx.android.synthetic.main.activity_passport_import.*
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials

class PassportImportActivity : BindActivity<ActivityPassportImportBinding>(), TabLayout.OnTabSelectedListener {
    override val contentLayoutId: Int = R.layout.activity_passport_import

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        showFragmentOfTab(tab!!.position)
    }

    companion object {
        private const val TAG_PASTE_KEYSTORE = "paste_keystore"
        private const val TAG_PASTE_PRIVATE_KEY = "paste_private_key"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        ensurePassportIsAbsent()
        binding.tabsImport.addOnTabSelectedListener(this)
        showFragmentOfTab(tabs_import.selectedTabPosition)
        binding.btnImportSubmit.setOnClickListener {
            tryImport()
        }
    }

    private fun ensurePassportIsAbsent() {
        if (App.get().passportRepository.passportExists){
            // post action to complete onCreate() and postpone finish
            postOnMainThread {
                toast("已存在钱包")
                finish()
            }
        }
    }

    private fun tryImport() {
        tryReadPassport()
                .flatMap {
                    PassportOperations.enablePassport(it.first,it.second)
                }
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({ onImportSuccess() }, this::showError)
    }

    private fun onImportSuccess() {
        toast("导入成功")
        finish()
        navigateTo(PassportCreationSucceedActivity::class.java){
            putExtra(Extras.FROM_IMPORT,true)
        }
    }

    private fun showError(e: Throwable) {
        stackTrace(e)
        when (e) {
            is CipherException -> {
                toast("KeyStore或钱包密码有误,请重试")
            }
            is JsonSyntaxException -> {
                toast("KeyStore信息格式错误")
            }
            else -> {
                toast("导入失败")
            }
        }
    }

    private fun tryReadPassport(): Single<Pair<Credentials,String>> {
        return when (tabs_import.selectedTabPosition) {
            0 -> {
                val fragment = supportFragmentManager.findFragmentByTag(TAG_PASTE_KEYSTORE)
                (fragment as PasteKeystoreFragment).parsePassport()
            }
            1 -> {
                val fragment = supportFragmentManager.findFragmentByTag(TAG_PASTE_PRIVATE_KEY)
                (fragment as PastePrivateKeyFragment).parsePassport()
            }
            else -> Single.error(IllegalStateException())
        }
    }

    fun showFragmentOfTab(tabPosition: Int) {
        when (tabPosition) {
            0 -> {
                val fragment = supportFragmentManager.findOrCreateFragment(TAG_PASTE_KEYSTORE, PasteKeystoreFragment.Companion::create)
                replaceFragment(fragment, R.id.fl_import_area, TAG_PASTE_KEYSTORE)
            }
            1 -> {
                val fragment = supportFragmentManager.findOrCreateFragment(TAG_PASTE_PRIVATE_KEY, PastePrivateKeyFragment.Companion::create)
                replaceFragment(fragment, R.id.fl_import_area, TAG_PASTE_PRIVATE_KEY)
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}
