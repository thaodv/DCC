package io.wexchain.android.idverify

import android.text.TextUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Created by sisel on 2018/2/27.
 */
data class IdCardEssentialData(
        val name: String,
        val id: String,
        val expired: Long,
        val sex: String?,
        val race: String?,
        val year: Int?,
        val month: Int?,
        val dayOfMonth: Int?,
        val address: String?,
        val authority: String?
) {

    fun yearText(): String {
        return year?.toString() ?: ""
    }

    fun monthText(): String {
        return month?.toString() ?: ""
    }

    fun dayText(): String {
        return dayOfMonth?.toString() ?: ""
    }

    companion object {
        //GMT+8 2199/12/31 0:0:0
        const val ID_TIME_EXPIRATION_UNLIMITED = 7258003200000L
        private val timeLimitPattern =
//            Pattern.compile("^(19[\\d]{2}|2[\\d]{3})\\.([1-9]|1[0-2])\\.([1-9]|[1-2][0-9]|3[0-1])-(((19[\\d]{2}|2[\\d]{3})\\.([1-9]|1[0-2])\\.([1-9]|[1-2][0-9]|3[0-1]))|长期)$")
        // eg:20120625-20220625
                Pattern.compile("^(19[\\d]{2}|2[\\d]{3})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])-(((19[\\d]{2}|2[\\d]{3})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))|长期)$")

        fun from(
                name: String,
                id: String,
                timeLimit: String,
                sex: String?,
                race: String?,
                year: Int?,
                month: Int?,
                dayOfMonth: Int?,
                address: String?,
                authority: String?
        ): IdCardEssentialData {
            val expired = parseExpired(timeLimit)
            return IdCardEssentialData(
                    name, id, expired,
                    sex,
                    race,
                    year, month, dayOfMonth,
                    address,
                    authority)
        }

        fun parseExpired(timeLimit: String): Long {
            val matcher = timeLimitPattern.matcher(timeLimit)
            if (matcher.matches()) {
                //CN ID expiration
                return if ("长期" == matcher.group(4)) {
                    ID_TIME_EXPIRATION_UNLIMITED
                } else {
                    //has deterministic expiration
                    val y = matcher.group(6).toInt()
                    val m = matcher.group(7).toInt()
                    val d = matcher.group(8).toInt()
                    Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
                        timeInMillis = 0L//reset
                        set(y, m - 1, d)
                    }.timeInMillis
                }
            }
            return -1L
        }

        private fun isValidName(name: String): Boolean {
            return !TextUtils.isEmpty(name)
        }

        private fun isValidId(id: String): Boolean {
            return id.length == 15 || id.length == 18
        }
    }
}
