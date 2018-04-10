package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.drawable.Drawable

/**
 * Created by lulingzhi on 2017/12/11.
 */
class BottomMoreVm : ViewModel() {

    val noMoreHint = ObservableField<CharSequence>()
    val loadMoreHint = ObservableField<CharSequence>()
    val hasMoreIcon = ObservableField<Drawable>()
    val hasMore = ObservableBoolean()

}