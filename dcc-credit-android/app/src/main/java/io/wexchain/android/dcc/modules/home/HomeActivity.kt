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
        setDefaultFragment()
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

    private fun setDefaultFragment() {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.layFrame, ServiceFragment())
        transaction.commit()
    }

    private fun initView() {
        bottom_navigation_bar.apply {
            setMode(BottomNavigationBar.MODE_FIXED)
            setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            addItem(BottomNavigationItem(R.drawable.logo_bitexpress, "服务").setActiveColorResource(R.color.colorAccent))
            addItem(BottomNavigationItem(R.drawable.logo_bitexpress, "发现").setActiveColorResource(R.color.colorAccent))
            addItem(BottomNavigationItem(R.drawable.logo_bitexpress, "我的").setActiveColorResource(R.color.colorAccent))
//            setFirstSelectedPosition(0)
            initialise()
        }


    }

}
