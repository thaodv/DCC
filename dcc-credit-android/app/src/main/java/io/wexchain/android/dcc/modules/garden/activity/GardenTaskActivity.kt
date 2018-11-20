package io.wexchain.android.dcc.modules.garden.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGardentaskBinding
import io.wexchain.dccchainservice.type.TaskCode
import worhavah.tongniucertmodule.SubmitTNLogActivity

/**
 *Created by liuyang on 2018/11/2.
 */
class GardenTaskActivity : BindActivity<ActivityGardentaskBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_gardentask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()
        initEvent()
    }

    fun checkCert(type: CertificationType, event: () -> Unit) {
        val status = CertOperations.getCertStatus(type)
        if (status == UserCertStatus.NONE) {
            PassportOperations.ensureCaValidity(this@GardenTaskActivity) {
                event.invoke()
            }
        }
    }

    private fun initEvent() {
        binding.vm!!.run {
            toGardenList.observe(this@GardenTaskActivity, Observer {
                navigateTo(GardenListActivity::class.java)
            })
            shareWechat.observe(this@GardenTaskActivity, Observer {
                GardenOperations.shareWechat {
                    toast(it)
                }
            })
            idCert.observe(this@GardenTaskActivity, Observer {
                navigateTo(SubmitIdActivity::class.java)
            })
            bankCert.observe(this@GardenTaskActivity, Observer {
                checkCert(CertificationType.BANK) {
                    navigateTo(SubmitBankCardActivity::class.java)
                }
            })
            cmCert.observe(this@GardenTaskActivity, Observer {
                checkCert(CertificationType.MOBILE) {
                    navigateTo(SubmitCommunicationLogActivity::class.java)
                }
            })
            tnCert.observe(this@GardenTaskActivity, Observer {
                checkCert(CertificationType.TONGNIU) {
                    navigateTo(SubmitTNLogActivity::class.java)
                }
            })
            backWallet.observe(this@GardenTaskActivity, Observer {
                val dialog = BaseDialog(this@GardenTaskActivity)
                dialog.removePassportDialog("备份钱包", "点击【去备份钱包】完成备份钱包任务。备份完成，点击【已备份领阳光】，获得50阳光！", "去备份钱包", "已备份领阳光")
                dialog.onClick(
                        onCancle = {
                            dialog.dismiss()
                            toBackup()

                        },
                        onConfirm = {
                            dialog.dismiss()
                            getReward()
                        })
            })
            openIpfs.observe(this@GardenTaskActivity, Observer {
                navigateTo(OpenCloudActivity::class.java) {
                    putExtra("activity_type", PassportSettingsActivity.NOT_OPEN_CLOUD)
                }
            })
            syncIpfs.observe(this@GardenTaskActivity, Observer {
                isOpenIpfs.value?.let {
                    if (!it.first) {
                        navigateTo(OpenCloudActivity::class.java) {
                            putExtra("activity_type", PassportSettingsActivity.NOT_OPEN_CLOUD)
                        }
                    } else {
                        if (it.second.isNullOrEmpty()) {
                            navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                            }
                        } else {
                            val ipfsKeyHash = App.get().passportRepository.getIpfsKeyHash()
                            if (ipfsKeyHash == it.second) {
                                navigateTo(MyCloudActivity::class.java)
                            } else {
                                App.get().passportRepository.setIpfsKeyHash("")
                                navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun getReward() {
        GardenOperations.completeTask(TaskCode.BACKUP_WALLET)
                .subscribeBy {
                    binding.vm!!.refreshTaskList()
                }
    }

    override fun onResume() {
        super.onResume()
        binding.vm?.refreshTaskList()
    }

    private fun toBackup() {
        navigateTo(PassportExportActivity::class.java)
    }

}