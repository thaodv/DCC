package io.wexchain.android.dcc.modules.redpacket

import android.annotation.SuppressLint
import android.os.Bundle
import com.bumptech.glide.Glide
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.onLongSaveImageToGallery
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPosterBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

class PosterActivity : BindActivity<ActivityPosterBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_poster

    private val mStartTime get() = intent.getLongExtra(Extras.EXTRA_REDPACKET_START_TIME, 0)
    private val mEndTime get() = intent.getLongExtra(Extras.EXTRA_REDPACKET_END_TIME, 0)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        val palyerId = App.get().userInfo!!.player!!.id

        binding.tvTime.text = "活动时间 " + DateUtil.getStringTime(mStartTime, "yyyy/MM/dd") + " ~ " + DateUtil.getStringTime(mEndTime, "yyyy/MM/dd")

        binding.rlRoot.onLongSaveImageToGallery(binding.scRoot,
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "保存失败")
                },
                onSuccess = {
                    toast("已保存至: $it")
                })

        App.get().marketingApi
                .getRedPacketErCodeFirst(palyerId.toString())
                .check()
                .doMain()
                .subscribe({
                    Glide.with(binding.ivErcode).load(BuildConfig.DCC_MARKETING_API_URL + "/redpacket/codeImage?key=" + it)
                            .into(binding.ivErcode)
                }, {})


    }
}
