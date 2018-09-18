package io.wexchain.android.dcc.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.dcc.base.BaseCompatFragment
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment:BaseCompatFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

}