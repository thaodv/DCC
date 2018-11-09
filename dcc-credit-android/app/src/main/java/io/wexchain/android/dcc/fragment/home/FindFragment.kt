package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.DccAffiliateActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.garden.activity.GardenActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenListActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenTaskActivity
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentFindBinding


/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment : BindFragment<FragmentFindBinding>() {

    private val passport by lazy {
        App.get().passportRepository
    }

    private var dialog: BaseDialog? = null

    override val contentLayoutId: Int
        get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initVm()
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
        App.get().isLogin.observe(this, Observer {
            it?.let {
                if (it) {
                    if (!GardenOperations.isBound()) {
                        showBoundDialog()
                    } else {
                        binding.vm = getViewModel()
                    }
                }
            }
        })
    }

    private fun initClick() {
        binding.gardenTask.checkBoundClick {
            navigateTo(GardenTaskActivity::class.java)
        }
        binding.findInGarden.checkBoundClick {
            navigateTo(GardenActivity::class.java)
        }
        binding.findShare.checkBoundClick {
            GardenOperations.shareWechat(activity!!) {
                toast(it)
            }
        }
        binding.findGardenCard.checkBoundClick {
            navigateTo(GardenActivity::class.java)
        }
        binding.findGardenList.checkBoundClick {
            navigateTo(GardenListActivity::class.java)
        }
        binding.findGardenList2.checkBoundClick {
            navigateTo(GardenListActivity::class.java)
        }
        binding.findZhishiCard.checkBoundClick {

        }
    }

    fun View.checkBoundClick(event: () -> Unit) {
        this.onClick {
            if (GardenOperations.isBound()) {
                event()
            } else {
                showBoundDialog()
            }
        }
    }

    fun showBoundDialog() {
        if (dialog == null) {
            dialog = BaseDialog(activity!!)
        } else {
            dialog!!.show()
            return
        }
        if (dialog!!.isShowing) {
            return
        }
        dialog!!.BoundWechatDialog()
                .onClick(onConfirm = {
                    GardenOperations.wechatLogin {
                        toast(it)
                    }
                })
    }


}