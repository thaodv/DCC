package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
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

    companion object {
        private const val MATE_DATA = "mate_data"

        fun getInstance(data: String? = null): FindFragment {
            val fragment = FindFragment()
            fragment.arguments = Bundle().apply {
                putString(MATE_DATA, data)
            }
            return fragment
        }
    }

    private val passport by lazy {
        App.get().passportRepository
    }

    private val data by lazy {
        arguments?.getString(MATE_DATA)
    }

    private var dialog: BaseDialog? = null

    override val contentLayoutId: Int
        get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        login()
    }

    fun login() {
        GardenOperations.loginWithCurrentPassport()
                .withLoading()
                .subscribeBy {
                    initVm()
                    if (!data.isNullOrEmpty()) {
                        val list = data!!.split('/')
                        val code = list[0]
                        val playid = list[1]
                        val unionId = list[2]
                        val info = it.body()!!.result!!
                        if (null != info.player) {
                            if (info.player!!.id.toString() != playid || unionId != info.player!!.unionId) {//不是同一个用户
                                BaseDialog(activity!!).TipsDialog()
                            }
                        }
                    }
                }
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })

        if (!GardenOperations.isBound()) {
            showBoundDialog()
        } else {
            binding.vm = getViewModel()
        }
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
            if (GardenOperations.isLogin()) {
                if (GardenOperations.isBound()) {
                    event()
                } else {
                    showBoundDialog()
                }
            } else {
                toast("用户未登陆")
                login()
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