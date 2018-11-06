package io.wexchain.android.dcc.fragment.home

import android.os.Bundle
import android.view.View
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.garden.GardenTaskActivity
import io.wexchain.android.dcc.modules.home.GardenActivity
import io.wexchain.android.dcc.tools.Log
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentFindBinding

/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment : BindFragment<FragmentFindBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gardenTask.onClick {
            navigateTo(GardenTaskActivity::class.java)
        }
        binding.findUserGarden.onClick {
            navigateTo(GardenActivity::class.java)
        }
        login()
    }

    fun login() {
        GardenOperations.loginWithCurrentPassport()
                .subscribeBy {
                    val info = it.body()!!.result
//                    info?.profilePhoto?.let {
//                        App.get().passportRepository.saveAvatar()
//                    }

                    Log(info.toString())
                    toast(info.toString())
                }
    }

}