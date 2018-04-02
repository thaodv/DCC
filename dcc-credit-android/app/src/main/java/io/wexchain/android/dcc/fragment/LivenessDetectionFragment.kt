package io.wexchain.android.dcc.fragment

import io.wexchain.android.dcc.base.BaseCompatFragment
import io.wexchain.auth.R

class LivenessDetectionFragment:BaseCompatFragment() {

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_liveness_detection)
    }
}