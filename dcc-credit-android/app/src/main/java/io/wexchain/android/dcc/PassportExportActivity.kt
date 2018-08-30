package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.TabLayout
import io.wexchain.android.dcc.fragment.ExportPrivateKeyFragment
import io.wexchain.android.common.findOrCreateFragment
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.fragment.ExportKeystoreFragment
import io.wexchain.android.dcc.tools.getPrivateKey
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_passport_export.*

class PassportExportActivity : BaseCompatActivity(), TabLayout.OnTabSelectedListener {
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

    private lateinit var passport: Passport
    private lateinit var protect: Protect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport_export)
        initToolbar(true)
        val p = App.get().passportRepository.getCurrentPassport()
        if (p == null) {
            toast("fail to get passport info")
            finish()
            return
        }
        passport = p
        this.tabs_export.addOnTabSelectedListener(this)
        showFragmentOfTab(tabs_export.selectedTabPosition)
        protect = Protect(application)
        protect.protectChallengeEvent.observe(this, Observer { next ->
            next ?: return@Observer
            val listener: VerifyProtectFragment.Listener = object : VerifyProtectFragment.Listener {
                override fun onVerifySuccess() {
                    next.invoke(true)
                }

                override fun verifyCancelledNotSucceed() {
                    next.invoke(false)
                }
            }
            VerifyProtectFragment.create(listener).show(supportFragmentManager, null)
        })
        verified = false
    }

    private var verified = false
    fun showFragmentOfTab(tabPosition: Int) {
        when (tabPosition) {
            0 -> {
                showExportKeyStore()
            }
            1 -> {
                if(!verified){
                    protect.verifyProtect({
                        this.tabs_export.getTabAt(0)?.select()
                    }, {
                        verified = true
                        showExportPrivateKey()
                    })
                }else{
                    showExportPrivateKey()
                }
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    private fun showExportPrivateKey() {
        val fragment = supportFragmentManager.findOrCreateFragment(TAG_PASTE_PRIVATE_KEY, {
            ExportPrivateKeyFragment.create(passport.getPrivateKey()) })
        replaceFragment(fragment, R.id.fl_export_area, TAG_PASTE_PRIVATE_KEY)
    }

    private fun showExportKeyStore() {
        val fragment = supportFragmentManager.findOrCreateFragment(TAG_PASTE_KEYSTORE, { ExportKeystoreFragment.create(passport) })
        replaceFragment(fragment, R.id.fl_export_area, TAG_PASTE_KEYSTORE)
    }
}
