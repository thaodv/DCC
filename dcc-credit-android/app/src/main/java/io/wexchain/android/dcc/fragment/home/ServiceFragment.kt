package io.wexchain.android.dcc.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.wexchain.android.dcc.base.BaseCompatFragment
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/9/18.
 */
class ServiceFragment : BaseCompatFragment() {

    private lateinit var mBtBsx: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_service, container, false)

        mBtBsx = view.findViewById(R.id.bt_bsx)

        mBtBsx.setOnClickListener {
            startActivity(Intent(activity, BsxMarketActivity::class.java))
        }

        return view
    }


}
