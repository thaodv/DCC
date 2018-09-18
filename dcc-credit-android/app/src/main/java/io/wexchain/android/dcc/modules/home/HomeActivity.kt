package io.wexchain.android.dcc.modules.home

import android.os.Bundle
import android.support.v4.app.Fragment
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import io.wexchain.android.common.commitTransaction
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.fragment.home.FindFragment
import io.wexchain.android.dcc.fragment.home.MineFragment
import io.wexchain.android.dcc.fragment.home.ServiceFragment
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_home2.*


/**
 *Created by liuyang on 2018/9/18.
 */
class HomeActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        initView()
        replaceFragment(ServiceFragment())
        initEvent()
    }

    private fun initEvent() {
        bottom_navigation_bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabSelected(position: Int) {
                when (position) {
                    0 -> replaceFragment(ServiceFragment())
                    1 -> replaceFragment(FindFragment())
                    2 -> replaceFragment(MineFragment())
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commitTransaction {
            replace(R.id.layFrame, fragment)
        }
    }

    private fun initView() {
        bottom_navigation_bar.apply {
            setMode(BottomNavigationBar.MODE_FIXED)
            setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            addItem(BottomNavigationItem(R.drawable.tab_activity_service, "服务").setInactiveIconResource(R.drawable.tab_service).setActiveColorResource(R.color.main_tab))
            addItem(BottomNavigationItem(R.drawable.tab_activity_find, "发现").setInactiveIconResource(R.drawable.tab_find).setActiveColorResource(R.color.main_tab))
            addItem(BottomNavigationItem(R.drawable.tab_activity_mine, "我的").setInactiveIconResource(R.drawable.tab_mine).setActiveColorResource(R.color.main_tab))
            initialise()
        }


    }

}
