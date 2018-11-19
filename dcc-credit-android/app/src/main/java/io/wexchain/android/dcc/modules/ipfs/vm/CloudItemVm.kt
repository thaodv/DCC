package io.wexchain.android.dcc.modules.ipfs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.modules.ipfs.ActionType
import io.wexchain.android.dcc.modules.ipfs.EventType
import io.wexchain.android.dcc.modules.ipfs.IpfsStatus

/**
 *Created by liuyang on 2018/11/15.
 */
class CloudItemVm : ViewModel() {

    val state = ObservableField<IpfsStatus>()

    val action = ObservableField<ActionType>()
            .apply { this.set(ActionType.STATUS_SELECT) }

    val event = ObservableField<EventType>()
            .apply { this.set(EventType.STATUS_DEFAULT) }

    val name = ObservableField<String>()

    val size = ObservableField<String>()

    val progress = ObservableField<Int>()

    val addressCall = SingleLiveEvent<Void>()

    val itemCall = SingleLiveEvent<Void>()


    fun addressClick() {
        addressCall.call()
    }

    fun itemClick() {
        if (state.get() == IpfsStatus.STATUS_UPLOAD || state.get() == IpfsStatus.STATUS_DOWNLOAD) {
            if (action.get() == ActionType.STATUS_SELECT) {
                action.set(ActionType.STATUS_NOSELECT)
            } else {
                action.set(ActionType.STATUS_SELECT)
            }
            itemCall.call()
        }
    }


}