package io.wexchain.android.dcc.modules.redpacket

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.tools.onLongSaveImageToGallery
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPosterBinding
import io.wexchain.dccchainservice.DccChainServiceException

class PosterActivity : BindActivity<ActivityPosterBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_poster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.rlRoot.onLongSaveImageToGallery(binding.scRoot,
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "保存失败")
                },
                onSuccess = {
                    toast("已保存至: $it")
                })

    }
}
