package io.wexchain.android.dcc.modules.ipfs

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.wexchain.android.dcc.tools.getString
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/11/15.
 */
object IpfsHelper {

    @JvmStatic
    fun Context.getStatusImg(status: IpfsStatus?, action: ActionType?): Drawable? {
        if (null == action) {
            ContextCompat.getDrawable(this, R.drawable.cloud_item_noselected)
        }
        val drawableId = when (status) {
            null -> R.drawable.cloud_item_noselected
            IpfsStatus.STATUS_DOWNLOAD, IpfsStatus.STATUS_UPLOAD -> if (action == ActionType.STATUS_SELECT) R.drawable.cloud_item_selected else R.drawable.cloud_item
            IpfsStatus.STATUS_NEWEST, IpfsStatus.STATUS_RECERTIFICATION -> R.drawable.cloud_item_noselected
        }
        return ContextCompat.getDrawable(this, drawableId)
    }

    @JvmStatic
    fun getSizeStatus(status: IpfsStatus?): Boolean {
        return status?.let {
            when (status) {
                IpfsStatus.STATUS_UPLOAD -> true
                else -> false
            }
        } ?: false
    }

    @JvmStatic
    fun getSeleStatus(status: IpfsStatus?): Boolean {
        return status?.let {
            when (status) {
                IpfsStatus.STATUS_DOWNLOAD, IpfsStatus.STATUS_UPLOAD -> true
                IpfsStatus.STATUS_NEWEST, IpfsStatus.STATUS_RECERTIFICATION -> false
            }
        } ?: false
    }

    @JvmStatic
    fun getAddressStatus(status: IpfsStatus?): Boolean {
        return status?.let {
            when (status) {
                IpfsStatus.STATUS_NEWEST -> true
                else -> false
            }
        } ?: false
    }

    @JvmStatic
    fun getProgressStatus(status: EventType?): Boolean {
        return status?.let {
            when (status) {
                EventType.STATUS_DOWNLOADING, EventType.STATUS_UPLOADING -> true
                else -> false
            }
        } ?: false
    }


    @JvmStatic
    fun getSeleTxt(status: IpfsStatus?, action: ActionType?, eventType: EventType?): String {
        return if ((status == IpfsStatus.STATUS_DOWNLOAD || status == IpfsStatus.STATUS_UPLOAD) && action == ActionType.STATUS_NOSELECT && eventType == EventType.STATUS_DEFAULT) {
            getString(R.string.ipfs_help_text1)
        } else if (status == IpfsStatus.STATUS_DOWNLOAD && action == ActionType.STATUS_SELECT && eventType == EventType.STATUS_DEFAULT) {
            getString(R.string.ipfs_help_text2)
        } else if (status == IpfsStatus.STATUS_UPLOAD && action == ActionType.STATUS_SELECT && eventType == EventType.STATUS_DEFAULT) {
            getString(R.string.ipfs_help_text3)
        } else if (eventType == EventType.STATUS_UPLOADING) {
            getString(R.string.ipfs_help_text4)
        } else if (eventType == EventType.STATUS_DOWNLOADING) {
            getString(R.string.ipfs_help_text5)
        } else if (status == IpfsStatus.STATUS_UPLOAD && eventType == EventType.STATUS_COMPLETE) {
            getString(R.string.ipfs_help_text6)
        } else if (status == IpfsStatus.STATUS_DOWNLOAD && eventType == EventType.STATUS_COMPLETE) {
            getString(R.string.ipfs_help_text6_1)
        } else {
            ""
        }
    }

    @JvmStatic
    fun getStatusTxt(status: IpfsStatus?): String {
        return status?.let {
            when (status) {
                IpfsStatus.STATUS_DOWNLOAD -> getString(R.string.ipfs_help_text7)
                IpfsStatus.STATUS_RECERTIFICATION -> getString(R.string.ipfs_help_text8)
                IpfsStatus.STATUS_NEWEST -> getString(R.string.ipfs_help_text9)
                IpfsStatus.STATUS_UPLOAD -> getString(R.string.ipfs_help_text10)
                else -> ""
            }
        } ?: ""
    }


}
