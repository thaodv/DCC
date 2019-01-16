package io.wexchain.android.dcc.modules.garden

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import io.wexchain.android.dcc.tools.formatText
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.TaskList
import io.wexchain.dccchainservice.domain.WeekRecord
import io.wexchain.dccchainservice.type.MathType
import io.wexchain.dccchainservice.type.StatusType
import io.wexchain.dccchainservice.type.TaskCode
import java.text.SimpleDateFormat
import java.util.*

/**
 *Created by liuyang on 2018/11/7.
 */
object TaskHelper {

    private val expiredFormat = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA)

    @JvmStatic
    fun expiredText(expired: Long?): String {
        if (expired == null || expired <= 0) {
            return ""
        }
        return expiredFormat.format(expired)
    }

    @JvmStatic
    fun Context.isApplyback(isapply: Boolean): Drawable? {
        return if (isapply) {
            ContextCompat.getDrawable(this, R.drawable.round_rect_fill_gray)
        } else {
            ContextCompat.getDrawable(this, R.drawable.background_splash)
        }
    }

    @JvmStatic
    fun isApplytxt(isapply: Boolean): String {
        return if (isapply) {
            "已签到"
        } else {
            "签到"
        }
    }

    @JvmStatic
    fun signDay(list: List<WeekRecord>?): String {
        return "已签到${list?.size ?: 0}天"
    }

    @JvmStatic
    fun Context.signDayBack(day: Int, list: List<WeekRecord>?): Drawable? {
        return if (day <= list?.size ?: 0) {
            ContextCompat.getDrawable(this, R.drawable.background_task)
        } else {
            ContextCompat.getDrawable(this, R.drawable.background_task2)
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.signDayColor(day: Int, list: List<WeekRecord>?): Int {
        return if (day <= list?.size ?: 0) {
            ContextCompat.getColor(this, R.color.FFFC318C)
        } else {
            ContextCompat.getColor(this, R.color.white)
        }
    }

    @JvmStatic
    fun getStatusTxt(task: TaskList.Task?): String {
        return if (task == null) {
            ""
        } else when {
            task.status == StatusType.FULFILLED -> "已完成"
            task.status == StatusType.UNFULFILLED ->
                when (task.code) {
                    TaskCode.BACKUP_ID,
                    TaskCode.BACKUP_BANK_CARD,
                    TaskCode.BACKUP_COMMUNICATION_LOG,
                    TaskCode.BACKUP_TN_COMMUNICATION_LOG,
                    TaskCode.BACKUP_WALLET,
                    TaskCode.OPEN_CLOUD_STORE,
                    TaskCode.ID,
                    TaskCode.BANK_CARD,
                    TaskCode.COMMUNICATION_LOG,
                    TaskCode.TN_COMMUNICATION_LOG -> task.bonus + " 〉"
                    else -> task.bonus
                }
            else -> ""
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.getStatusColor(task: TaskList.Task?): Int {
        return if (task == null) {
            ContextCompat.getColor(this, R.color.FF7ED321)
        } else when {
            task.status == StatusType.FULFILLED -> ContextCompat.getColor(this, R.color.FF7ED321)
            task.status == StatusType.UNFULFILLED -> ContextCompat.getColor(this, R.color.FF7B40FF)
            else -> ContextCompat.getColor(this, R.color.FF7ED321)
        }
    }

    @JvmStatic
    fun getItemNum(math: MathType, num: String): String {
        return if (math == MathType.PLUS) {
            "+$num 阳光"
        } else {
            "-$num 阳光"
        }
    }

    @JvmStatic
    fun getZhishimsg(data: ChangeOrder?): String {
        return "${data?.nickName ?: "王思聪"}获胜  +${data?.amount ?: "5"}阳光值"
    }

    @JvmStatic
    fun getGardenmsg(ismsg: Boolean?): String {
        return if (ismsg == true) "您有奖励未收取哦" else "您的奖励将于3小时后发放"
    }

    @JvmStatic
    fun getZhishiTime(data: ChangeOrder?): String {
        return data?.lastUpdatedTime?.formatText() ?: ""
    }

}
