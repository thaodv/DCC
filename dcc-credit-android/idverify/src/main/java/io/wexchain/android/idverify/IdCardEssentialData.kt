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
        private val timeLimitPattern =
            Pattern.compile("^(19[\\d]{2}|2[\\d]{3})\\.([1-9]|1[0-2])\\.([1-9]|[1-2][0-9]|3[0-1])-(19[\\d]{2}|2[\\d]{3})\\.([1-9]|1[0-2])\\.([1-9]|[1-2][0-9]|3[0-1])$")

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
        ): IdCardEssentialData? {
            val expired = parseExpired(timeLimit)
            if (isValidName(name) && isValidId(id) && expired > 0) {
                return IdCardEssentialData(
                    name, id, expired,
                    sex,
                    race,
                    year, month, dayOfMonth,
                    address,
                    authority
                )
            }
            return null
        }

        private fun parseExpired(timeLimit: String): Long {
            val matcher = timeLimitPattern.matcher(timeLimit)
            if (matcher.matches()) {
                val y = matcher.group(4).toInt()
                val m = matcher.group(5).toInt()
                val d = matcher.group(6).toInt()
                //CN ID expiration eg: 2007.11.12-2027.11.11
                return Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
                    timeInMillis = 0L//reset
                    set(y, m, d)
                }.timeInMillis
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
