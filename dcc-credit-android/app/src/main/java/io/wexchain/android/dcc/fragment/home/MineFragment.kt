package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.modules.mine.ModifyPassportPasswordActivity
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.modules.passport.PassportExportActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketSettingsActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentMineBinding
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/9/18.
 */
class MineFragment : BindFragment<FragmentMineBinding>() {

    private val passport by lazy {
        App.get().passportRepository
    }

    var isOpenTrustPocket: Boolean = false

    override val contentLayoutId: Int get() = R.layout.fragment_mine

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        initVm()
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
    }

    override fun onResume() {
        super.onResume()
        checkBoundWechat()
        getCloudToken()
        getHostingWallet()
    }

    private fun checkBoundWechat() {
        binding.tvWechatStatus.text = if (GardenOperations.isBound()) "已授权" else "未授权"
        binding.tvUserWechat.onClick {
            GardenOperations.wechatLogin {
                toast(it)
            }
        }
    }

    private fun getHostingWallet() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 已开户
                    if (it?.mobileUserId != null) {
                        isOpenTrustPocket = true
                        App.get().mobileUserId = it.mobileUserId

                    } else {
                        isOpenTrustPocket = false
                    }
                }, {
                    // 未开户
                    isOpenTrustPocket = false
                    toast(it.message.toString())
                })
    }

    private fun getCloudToken() {
        val anim = AnimationUtils.loadAnimation(activity!!, R.anim.rotate)
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .doOnSubscribe {
                    binding.ivCloudLoding.startAnimation(anim)
                    binding.ivCloudLoding.visibility = View.VISIBLE
                    binding.tvCloudStatus.visibility = View.INVISIBLE
                }
                .doFinally {
                    binding.ivCloudLoding.clearAnimation()
                    binding.ivCloudLoding.visibility = View.INVISIBLE
                    binding.tvCloudStatus.visibility = View.VISIBLE
                }
                .subscribeBy {
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    binding.tvCloudStatus.text = if (it.isEmpty()) getString(R.string.start_in) else getString(R.string.start_out)
                    binding.tvDataCloud.onClick {
                        if (it.isEmpty()) {
                            activity?.navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", SettingActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                activity?.navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    activity?.navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    activity?.navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                    }
                                }
                            }
                        }
                    }
                }
    }

    private fun setupClicks() {
        val binding = binding
        binding.tvAddressBook.onClick {
            navigateTo(AddressBookActivity::class.java)
        }
        binding.tvSetting.onClick {
            navigateTo(SettingActivity::class.java)
        }
        binding.tvPassportBackup.onClick {
            navigateTo(PassportExportActivity::class.java)
        }
        binding.tvModifyPassportPassword.onClick {
            navigateTo(ModifyPassportPasswordActivity::class.java)
        }
        binding.tvDigestPocketSetting.onClick {
            navigateTo(ModifyPassportPasswordActivity::class.java)
        }
        binding.tvTrustPocketSetting.onClick {
            if (isOpenTrustPocket) {
                navigateTo(TrustPocketSettingsActivity::class.java)
            } else {
                navigateTo(TrustPocketOpenTipActivity::class.java)
            }
        }
    }

}
