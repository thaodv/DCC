package io.wexchain.android.dcc.modules.garden.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.MyCreditNewActivity
import io.wexchain.android.dcc.PassportExportActivity
import io.wexchain.android.dcc.PassportSettingsActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGardentaskBinding
import io.wexchain.dccchainservice.type.TaskCode

/**
 *Created by liuyang on 2018/11/2.
 */
class GardenTaskActivity : BindActivity<ActivityGardentaskBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_gardentask

    private val mActivity: GardenTaskActivity by lazy {
        this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initEvent()
    }

    private fun initVm() {
        binding.vm = getViewModel()
        binding.srlList.apply {
            isEnableLoadMore = false
            setOnRefreshListener {
                binding.vm!!.refreshTaskList { it.finishRefresh() }
            }
            setRefreshHeader(ClassicsHeader(mActivity))
        }
    }


    private fun initEvent() {
        binding.vm!!.run {
            toGardenList.observe(mActivity, Observer {
                navigateTo(GardenListActivity::class.java)
            })
            shareWechat.observe(mActivity, Observer {
                GardenOperations.shareWechat {
                    toast(it)
                }
            })
            backWallet.observe(mActivity, Observer {
                val dialog = BaseDialog(mActivity)
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
            openIpfs.observe(mActivity, Observer {
                navigateTo(OpenCloudActivity::class.java) {
                    putExtra("activity_type", PassportSettingsActivity.NOT_OPEN_CLOUD)
                }
            })
            syncIpfs.observe(mActivity, Observer {
                navigateTo(MyCreditNewActivity::class.java)
            })
        }
    }

    override fun onRestart() {
        super.onRestart()
        binding.vm?.refreshTaskList()
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